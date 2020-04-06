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

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.ISchedule;
import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.common.MethodUtil;
import com.yametech.yangjian.agent.core.config.Config;
import com.yametech.yangjian.agent.core.core.agent.AgentTransformer;
import com.yametech.yangjian.agent.core.core.agent.IContextField;
import com.yametech.yangjian.agent.core.core.interceptor.ContextInterceptor;
import com.yametech.yangjian.agent.core.metric.consume.RTEventListener;
import com.yametech.yangjian.agent.core.old.MethodEvent;
import com.yametech.yangjian.agent.core.util.AgentPath;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.matcher.ElementMatchers;

public class TestAll {
	
	@org.junit.Test
	public void testByteBuddy() throws InstantiationException, IllegalAccessException {
		Class<?> dynamicType = new ByteBuddy()
                .subclass(Object.class)
                .defineField(AgentTransformer.OBJECT_CONTEXT_FIELD_NAME, Map.class, Opcodes.ACC_PRIVATE)
                .implement(IContextField.class)
                .intercept(FieldAccessor.ofField(AgentTransformer.OBJECT_CONTEXT_FIELD_NAME))
                .implement(IContext.class)
                .method(ElementMatchers.named("_getAgentContext").or(ElementMatchers.named("_setAgentContext")))
                .intercept(MethodDelegation.to(ContextInterceptor.class))
//                .intercept(MethodDelegation.to(ContextMapInterceptor.class))
                .make()
                .load(TestAll.class.getClassLoader())
                .getLoaded();

		// ContextMapInterceptor	17189	不创建Map：1370	1180	1396	1188
		// ContextInterceptor	1873	不创建Map：632	649	646	631
		long start = System.currentTimeMillis();
		for(int i = 0; i < 20000000; i++) {
			IContext instance = (IContext) dynamicType.newInstance();
			instance._setAgentContext("key", "test" + i);
			Object context = instance._getAgentContext("key");
//			System.out.println(instance._getAgentContext("key"));
//			System.out.println(instance.getClass().getCanonicalName());
//			System.out.println(instance);
//			System.out.println("\n\n");
		}
		System.err.println(System.currentTimeMillis() - start);
	}
	
	@org.junit.Test
	public void path() {
		File path = AgentPath.getPath();
		System.err.println(path.getAbsolutePath());
		System.err.println(1 << 15);
		int bufferSize = 1 << 3;
		System.err.println(bufferSize);
		
		long start = System.currentTimeMillis();
		for(int i = 0; i < 1000; i++) {
			int index = i & (8 - 1);
//			int index = i % 8;
//			System.err.println(index);
		}
		System.err.println(System.currentTimeMillis() - start);
		
		
		System.err.println(Pattern.matches("test", "test"));
		
		System.err.println(1 << 16);
		
		
		List<Integer> list = Arrays.asList(1,2,3,4,5).stream().map(num -> num > 3 ? null : num).collect(Collectors.toList());
		System.err.println(list);
	}
	
	
	@org.junit.Test
	public void processId() throws InterruptedException {
	    System.out.println("==== APP  STARTED ====");
	    RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
	    String name = runtime.getName();
	    System.out.println(name);
	    System.out.println("Process ID: " + name.substring(0, name.indexOf("@")));
	    Thread.sleep(100000);
	}
	
	@org.junit.Test
	public void memory() throws InterruptedException {
	    System.err.println(Runtime.getRuntime().freeMemory());// 空闲内存
	    System.err.println(Runtime.getRuntime().totalMemory());// 总内存
	    System.err.println(Runtime.getRuntime().maxMemory());// 最大内存
	    
	    int j = 0;
	    while (true) { // Noncompliant; end condition omitted
	      j++;
	    }
	}
	
	@org.junit.Test
	public void test() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		System.err.println(Config.CALL_EVENT_BUFFER_SIZE.getValue());
		System.err.println(RTEventListener.class.isAssignableFrom(ISchedule.class));
		/*System.err.println(IConfigReader.class.isAssignableFrom(CallEventAOP.class));
		System.err.println(IMethodAOP.class.isAssignableFrom(CallEventAOP.class));
		System.err.println(CallEventAOP.class.isAssignableFrom(CallEventAOP.class));*/
		System.err.println(ISchedule.class.isAssignableFrom(new RTEventListener().getClass()));
		System.err.println(RTEventListener.class.isAssignableFrom(new RTEventListener().getClass()));
		
		System.err.println(String.class.getName() + "--------" + String.class.getTypeName());
		System.err.println(MethodEvent.class.getName() + "--------" + MethodEvent.class.getTypeName());
		System.err.println(String[].class.getName() + "--------" + String[].class.getTypeName());
		
		Class stringArrayClass = Class.forName("[Ljava.lang.String;");
		System.err.println(stringArrayClass);
		
		Class<?> cls = Class.forName("com.alibaba.dubbo.rpc.proxy.InvokerInvocationHandler");
		List<Class<?>> argCls = new ArrayList<Class<?>>();
		argCls.add(Class.forName("java.lang.Object"));
		argCls.add(Class.forName("java.lang.reflect.Method"));
		argCls.add(Class.forName("[Ljava.lang.Object;"));
		System.err.println(cls.getMethod("invoke", argCls.toArray(new Class[0])));
	}
	
	/**
	 * 测试new创建对象与newInstance的性能对比
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@org.junit.Test
	public void testNew() throws InstantiationException, IllegalAccessException {
		long start = System.currentTimeMillis();
		for(int i = 0; i < 100000000; i++) {
//			CallEventAOP consume = CallEventAOP.class.newInstance();
			//CallEventAOP consume = new CallEventAOP();
		}
		System.err.println(System.currentTimeMillis() - start);
	}
	
	@org.junit.Test
	public void testMethod() {
		System.err.println(IConfigReader.class.getDeclaredMethods()[0]);
		System.err.println(this.getClass().getDeclaredMethods()[1].toString());
		System.err.println(this.getClass().getDeclaredMethods()[1].toGenericString());
		Method m = this.getClass().getDeclaredMethods()[0];
		long start4 = System.currentTimeMillis();
		for(int i = 0; i < 6000000; i++) {
			String mStr = m.toGenericString();
		}
		System.err.println(System.currentTimeMillis() - start4);
		
		long start = System.currentTimeMillis();
		for(int i = 0; i < 6000000; i++) {
			String mStr = m.toString();
		}
		System.err.println(System.currentTimeMillis() - start);
		
		long start3 = System.currentTimeMillis();
		for(int i = 0; i < 6000000; i++) {
			String mStr = MethodUtil.getId(m);
		}
		System.err.println(System.currentTimeMillis() - start3);
		
		Method m2 = this.getClass().getDeclaredMethods()[0];
		System.err.println(m2 == m);
		
		long start2 = System.currentTimeMillis();
		for(int i = 0; i < 6000000; i++) {
			boolean match = m.equals(m2);
		}
		System.err.println(System.currentTimeMillis() - start2);
		
		System.err.println(m2.getName());
		
	}
	
	public void method(String arg1, int arg2) throws TimeoutException  {
		throw new TimeoutException("测试异常");
	}

}
