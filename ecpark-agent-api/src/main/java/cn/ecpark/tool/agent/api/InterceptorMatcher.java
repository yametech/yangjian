package cn.ecpark.tool.agent.api;

import cn.ecpark.tool.agent.api.base.IMatch;
import cn.ecpark.tool.agent.api.base.IWeight;
import cn.ecpark.tool.agent.api.base.MethodType;
import cn.ecpark.tool.agent.api.base.SPI;
import cn.ecpark.tool.agent.api.bean.LoadClassKey;

public interface InterceptorMatcher extends IMatch, IWeight, SPI {
	
	/**
	 * 返回拦截器的类路径，如:cn.ecpark.tool.javaagent.YMAgent，可包含interceptor包下任意一个接口的实现类
	 * 	因interceptor类中可能会依赖应用中的类，为了避免出现ClassNotFound，interceptor的实例使用依赖类加载的classLoader初始化，而InterceptorMatcher子类不需要，所以拆分为两个类定义
	 * @return
	 */
	LoadClassKey loadClass(MethodType type);
}
