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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

public class MethodEventHandler4 implements EventHandler<MethodEvent>, WorkHandler<MethodEvent> {
	private static final Logger log = LoggerFactory.getLogger(MethodEventHandler4.class);

	@Override
	public void onEvent(MethodEvent event) throws Exception {
		execute(event);
	}

	@Override
	public void onEvent(MethodEvent event, long sequence, boolean endOfBatch) throws Exception {
		execute(event);
	}

	private void execute(MethodEvent event) {
		log.info("consume:{}", event);
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
}
