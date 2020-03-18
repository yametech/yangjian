/*
 * Copyright 2020 yametech.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yametech.yangjian.agent.util.disruptor;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.RingBuffer;

public class EventProducer {
	private static Logger log = LoggerFactory.getLogger(EventProducer.class);
	private RingBuffer<MethodEvent> ringBuffer;

	public EventProducer(RingBuffer<MethodEvent> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	public boolean onData(Method method, Object[] arguments, Long eventTime, Long startTime, Throwable throwable) {
//		log.info("1");
		long sequence = ringBuffer.next();
//		log.info("2");
		try {
			MethodEvent event = ringBuffer.get(sequence);
//			log.info("3");
			setFields(event, method, arguments, eventTime, startTime, throwable);
		} finally {
			ringBuffer.publish(sequence);
//			log.info("4");
		}
		return true;
	}
	
	private MethodEvent setFields(MethodEvent event, Method method, Object[] arguments, Long eventTime, Long startTime, Throwable throwable) {
		event.setMethod(method);
		event.setArguments(arguments);
		event.setEventTime(eventTime);
		event.setStartTime(startTime);
		event.setThrowable(throwable);
		return event;
	}
}
