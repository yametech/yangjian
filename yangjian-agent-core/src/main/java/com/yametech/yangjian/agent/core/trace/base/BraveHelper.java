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
package com.yametech.yangjian.agent.core.trace.base;

import com.yametech.yangjian.agent.api.common.Constants;

import brave.Tracing;
import brave.Tracing.Builder;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.StrictScopeDecorator;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.sampler.Sampler;
import zipkin2.Span;
import zipkin2.reporter.Reporter;

public class BraveHelper {
	public static final String EXTRA_KEY = "user-id";
	
	/**
	 * 获取trace实例
	 * @param spanReporter
	 * @param sampler
	 * @return
	 */
	public static Tracing getTracing(Reporter<Span> spanReporter, Sampler sampler) {
		Builder builder = Tracing.newBuilder()
				.localServiceName(Constants.serviceName())
				.spanReporter(spanReporter)
				.propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, EXTRA_KEY))
				.currentTraceContext(ThreadLocalCurrentTraceContext.newBuilder()
				      .addScopeDecorator(StrictScopeDecorator.create())
				      .build());
		if(sampler != null) {
			builder.sampler(sampler);
		}
		return builder.build();
	}
	
}
