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
package com.yametech.yangjian.agent.api.trace;

import brave.Tracer;

/**
 * 链路Span采样及tag定制
 * 
 * @author liuzhao
 */
@FunctionalInterface
public interface ISpanSample {
	
	/**
	 * 
	 * @param tracer	brave链路实例
	 * @return	true：生成Span，false：不生产span
	 */
	boolean sample(Tracer tracer);
	
}
