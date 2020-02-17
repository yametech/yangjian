package cn.ecpark.tool.agent.core.core.interceptor;

import cn.ecpark.tool.agent.api.interceptor.IConstructorListener;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

public class YmInstanceConstructorInterceptor {
//	private static final ILogger log = LoggerFactory.getLogger(YmInstanceConstructorInterceptor.class);
//	private static final MethodType TYPE = MethodType.CONSTRUCT;
	private IConstructorListener[] interceptors;

	public YmInstanceConstructorInterceptor(IConstructorListener[] interceptors) {
		this.interceptors = interceptors;
	}
	
//	public YmInstanceConstructorInterceptor(List<InterceptorMatcher> interceptors, ClassLoader classLoader, MethodDescription.InDefinedShape inDefinedShape) {
//		this.interceptors = interceptors.stream().map(matcher -> {
//			LoadClassKey loadClass = matcher.loadClass(TYPE);
//			if(loadClass == null) {
//				return null;// TODO 需测试是否可以返回null
//			}
//			try {
//				Object obj = InterceptorInstanceLoader.load(loadClass.getKey(), loadClass.getCls(), classLoader);
//				if(obj instanceof SPI) {
//					throw new IllegalStateException("不能实现SPI接口");
//				}
//				if(!(obj instanceof IConstructorListener)) {
//					throw new IllegalStateException("必须实现IConstructorListener");
//				}
//				if(matcher instanceof IInterceptorInit) {
//					((IInterceptorInit)matcher).init(obj, classLoader, TYPE);
//				}
//				log.debug("classLoader:{}	{}	{}	{}", obj, classLoader, loadClass, inDefinedShape);
//				return obj;
//			} catch (IllegalAccessException | InstantiationException | ClassNotFoundException
//					| AgentPackageNotFoundException e) {
//				log.warn(e, "加载实例异常{}", loadClass);
//				return null;// TODO 需测试是否可以返回null
//			}
//		}).toArray(IConstructorListener[]::new);
//	}

    @RuntimeType
    public void intercept(@This Object thisObj, @AllArguments Object[] allArguments) throws Throwable {
		for (IConstructorListener interceptor : interceptors) {
//      if (!methodInterceptor.enable()) {
//          continue;
//      }
			try {
				interceptor.constructor(thisObj, allArguments);
			} catch (Throwable t) {
//				log.warn(t, "interceptor constructor");// 业务异常，不打印
			}
		}

	}

}
