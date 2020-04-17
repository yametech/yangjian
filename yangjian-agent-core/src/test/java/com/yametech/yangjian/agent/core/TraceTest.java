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
package com.yametech.yangjian.agent.core;

import org.junit.Assert;

import com.yametech.yangjian.agent.core.trace.sample.RateLimitSampler;

public class TraceTest {
	
	@org.junit.Test
	public void test() throws Exception {
		System.err.println(System.currentTimeMillis() + ": " + ResourceHolder.print());
		Thread.sleep(1300);
		System.err.println(System.currentTimeMillis() + ": " + ResourceHolder.resource.print());
	}
	
	private static class ResourceHolder {
        public static Resource resource = new Resource();
        
        private static String print() {
        	return "445566";
        }
    }
	
	static class Resource {
		public Resource() {
			System.err.println(System.currentTimeMillis() + ": " + "init Resource");
		}
		
		public String print() {
			return "123";
		}
	}
	
	@org.junit.Test
	public void testSample() throws Exception {
		int sample = 10;
		RateLimitSampler sampler = new RateLimitSampler(sample);// 每秒10个样本，并且均匀分布在一秒钟
		
		for(int i = 0; i < 3 * sample; i++) {
			Assert.assertTrue(sampler.sample());
			Thread.sleep(101);// 每次睡眠101ms,保证每次sample都是true
		}
		
		sampler.sample();
		for(int i = 0; i < sample - 1; i++) {
			Assert.assertFalse(sampler.sample());// 因集中取值，都是false
		}
	}
}
