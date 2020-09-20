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
package com.yametech.yangjian.agent.plugin.dubbo.trace;

import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.configmatch.ClassMatch;
import com.yametech.yangjian.agent.api.configmatch.CombineAndMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodArgumentNumMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodNameMatch;
import com.yametech.yangjian.agent.api.trace.ITraceMatcher;
import com.yametech.yangjian.agent.api.trace.TraceType;

import java.util.Arrays;

public abstract class DubboServerTraceMatcher implements ITraceMatcher {

	@Override
	public TraceType type() {
		return TraceType.DUBBO_SERVER;
	}

	public static class ApacheDubboServerTraceMatcher extends DubboServerTraceMatcher {
		@Override
		public IConfigMatch match() {
			return new CombineAndMatch(Arrays.asList(
					new ClassMatch("org.apache.dubbo.monitor.support.MonitorFilter"),
					new MethodNameMatch("invoke"),
					new MethodArgumentNumMatch(2)
			));
		}

		@Override
		public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
			return new LoadClassKey("com.yametech.yangjian.agent.plugin.dubbo.trace.ApacheDubboServerSpanCreater");
		}
	}

	public static class AlibabaDubboServerTraceMatcher extends DubboServerTraceMatcher {
		@Override
		public IConfigMatch match() {
			return new CombineAndMatch(Arrays.asList(
					new ClassMatch("com.alibaba.dubbo.monitor.support.MonitorFilter"),
					new MethodNameMatch("invoke"),
					new MethodArgumentNumMatch(2)
			));
		}

		@Override
		public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
			return new LoadClassKey("com.yametech.yangjian.agent.plugin.dubbo.trace.AlibabaDubboServerSpanCreater");
		}
	}
}
