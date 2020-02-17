package cn.ecpark.tool.javaagent;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import cn.ecpark.tool.agent.api.IConfigReader;
import cn.ecpark.tool.agent.api.ISchedule;
import cn.ecpark.tool.agent.api.base.IContext;
import cn.ecpark.tool.agent.api.common.MethodUtil;
import cn.ecpark.tool.agent.core.aop.consume.RTEventListener;
import cn.ecpark.tool.agent.core.config.Config;
import cn.ecpark.tool.agent.core.core.agent.AgentTransformer;
import cn.ecpark.tool.agent.core.core.agent.IContextField;
import cn.ecpark.tool.agent.core.core.interceptor.ContextInterceptor;
import cn.ecpark.tool.agent.core.util.AgentPath;
import cn.ecpark.tool.agent.core.util.LogRateLimiter;
import cn.ecpark.tool.agent.core.util.RateLimiterHolder;
import cn.ecpark.tool.agent.core.util.SemaphoreLimiter;
import cn.ecpark.tool.javaagent.old.MethodEvent;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.matcher.ElementMatchers;

public class Test {
	
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
                .load(Test.class.getClassLoader())
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

	@org.junit.Test
	public void testLimiter() throws Exception {
		int threadCount = 1000;
		//List<Long> a = new ArrayList<>(threadCount);
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);
		//SemaphoreLimiter logLimiter = new SemaphoreLimiter(100, TimeUnit.SECONDS);
		RateLimiterHolder.register(RateLimiterHolder.LOG_RATE_LIMIT_KEY, new SemaphoreLimiter(2, TimeUnit.SECONDS));
		long s = System.currentTimeMillis();
		for (int i = 0; i < threadCount; i++) {
			new Thread(() -> {
				try {
					boolean tryAcquire = RateLimiterHolder.tryAcquire(RateLimiterHolder.LOG_RATE_LIMIT_KEY);
					countDownLatch.countDown();
					//a.add(System.currentTimeMillis() - s);
                    if (tryAcquire) {
                        System.out.println(Thread.currentThread().getName() + ",获取了成功，执行服务...");
                    } else {
                        System.out.println(Thread.currentThread().getName() + ",令牌没有了，待会再来吧...");
                    }
				} finally {
				}
			}).start();
		}

		countDownLatch.await();
		System.out.println("use:" + (System.currentTimeMillis() - s));
	}

	@org.junit.Test
	public void testLimiter2() throws Exception {
		int threadCount = 10;
		//List<Long> a = new ArrayList<>(threadCount);
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);
		LogRateLimiter logLimiter = new LogRateLimiter(100);
		long s = System.currentTimeMillis();
		for (int i = 0; i < threadCount; i++) {
			new Thread(() -> {
				boolean tryAcquire = false;
				try {
					tryAcquire = logLimiter.tryAcquire();
					//a.add(System.currentTimeMillis() - s);
					if (tryAcquire) {
						//System.out.println(Thread.currentThread().getName() + ",获取了成功，执行服务...");
					} else {
						// System.out.println(Thread.currentThread().getName() + ",令牌没有了，待会再来吧...");
					}
					countDownLatch.countDown();
				} finally {
				}
			}).start();
		}

		countDownLatch.await();
		System.out.println("use:" + (System.currentTimeMillis() - s));
	}


}
