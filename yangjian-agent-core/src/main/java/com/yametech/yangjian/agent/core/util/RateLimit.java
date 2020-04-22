/*
 * Copyright 2013-2019 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.yametech.yangjian.agent.core.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RateLimit {
	public static RateLimit create(int qps) {
		if (qps < 0)
			throw new IllegalArgumentException("qps < 0");
		return new RateLimit(qps);
	}

	static final long NANOS_PER_SECOND = TimeUnit.SECONDS.toNanos(1);
	static final long NANOS_PER_DECISECOND = NANOS_PER_SECOND / 10;

	final MaxFunction maxFunction;
	final AtomicInteger usage = new AtomicInteger(0);
	final AtomicLong nextReset;

	RateLimit(int tracesPerSecond) {
		this.maxFunction = tracesPerSecond < 10 ? new LessThan10(tracesPerSecond) : new AtLeast10(tracesPerSecond);
		long now = System.nanoTime();
		this.nextReset = new AtomicLong(now + NANOS_PER_SECOND);
	}

	public boolean tryAcquire() {
		long now = System.nanoTime(), updateAt = nextReset.get();

		// First task is to determine if this request is later than the one second
		// sampling window
		long nanosUntilReset = -(now - updateAt); // because nanoTime can be negative
		if (nanosUntilReset <= 0) {
			// Attempt to move into the next sampling interval.
			// nanosUntilReset is now invalid regardless of race winner, so we can't sample
			// based on it.
			if (nextReset.compareAndSet(updateAt, now + NANOS_PER_SECOND))
				usage.set(0);

			// recurse as it is simpler than resetting all the locals.
			// reset happens once per second, this code doesn't take a second, so no
			// infinite recursion.
			return tryAcquire();
		}

		// Now, we determine the amount of samples allowed for this interval, and sample
		// accordingly
		int max = maxFunction.max(nanosUntilReset);
		int prev, next;
		do { // same form as java 8 AtomicLong.getAndUpdate
			prev = usage.get();
			next = prev + 1;
			if (next > max)
				return false;
		} while (!usage.compareAndSet(prev, next));
		return true;
	}

	static abstract class MaxFunction {
		abstract int max(long nanosUntilReset);
	}

	/**
	 * For a reservoir of less than 10, we permit draining it completely at any time
	 * in the second
	 */
	static final class LessThan10 extends MaxFunction {
		final int tracesPerSecond;

		LessThan10(int tracesPerSecond) {
			this.tracesPerSecond = tracesPerSecond;
		}

		@Override
		int max(long nanosUntilResetIgnored) {
			return tracesPerSecond;
		}
	}

	/**
	 * For a reservoir of at least 10, we permit draining up to a decisecond
	 * watermark. Because the rate could be odd, we may have a remainder, which is
	 * arbitrarily available. We allow any remainders in the 1st decisecond or any
	 * time thereafter.
	 *
	 * <p>
	 * Ex. If the rate is 10/s then you can use 1 in the first decisecond, another 1
	 * in the 2nd, or up to 10 by the last.
	 *
	 * <p>
	 * Ex. If the rate is 103/s then you can use 13 in the first decisecond, another
	 * 10 in the 2nd, or up to 103 by the last.
	 */
	static final class AtLeast10 extends MaxFunction {
		final int[] max;

		AtLeast10(int tracesPerSecond) {
			int tracesPerDecisecond = tracesPerSecond / 10, remainder = tracesPerSecond % 10;
			max = new int[10];
			max[0] = tracesPerDecisecond + remainder;
			for (int i = 1; i < 10; i++) {
				max[i] = max[i - 1] + tracesPerDecisecond;
			}
		}

		@Override
		int max(long nanosUntilReset) {
			// Check to see if we are in the first or last interval
			if (nanosUntilReset > NANOS_PER_SECOND - NANOS_PER_DECISECOND)
				return max[0];
			if (nanosUntilReset < NANOS_PER_DECISECOND)
				return max[9];

			// Choose a slot based on the remaining deciseconds
			int decisecondsUntilReset = (int) (nanosUntilReset / NANOS_PER_DECISECOND);
			return max[10 - decisecondsUntilReset];
		}
	}
}
