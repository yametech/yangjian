package cn.ecpark.tool.agent.core.aop;

import cn.ecpark.tool.agent.api.IMetricMatcher;
import cn.ecpark.tool.agent.api.InterceptorMatcher;
import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.base.IInterceptorInit;
import cn.ecpark.tool.agent.api.base.MethodType;
import cn.ecpark.tool.agent.api.bean.LoadClassKey;
import cn.ecpark.tool.agent.api.convert.IConvertMatcher;
import cn.ecpark.tool.agent.core.aop.base.MetricEventBus;
import cn.ecpark.tool.agent.core.core.classloader.InterceptorInstanceLoader;
import cn.ecpark.tool.agent.core.exception.AgentPackageNotFoundException;
import cn.ecpark.tool.agent.core.log.ILogger;
import cn.ecpark.tool.agent.core.log.LoggerFactory;

public class MetricMatcherProxy implements IInterceptorInit, InterceptorMatcher {
	private static ILogger log = LoggerFactory.getLogger(MetricMatcherProxy.class);
	private IMetricMatcher metricMatcher;
	private MetricEventBus metricEventBus;
	
	public MetricMatcherProxy(IMetricMatcher metricMatcher, MetricEventBus metricEventBus) {
		this.metricMatcher = metricMatcher;
		this.metricEventBus = metricEventBus;
	}
	
	@Override
	public void init(Object obj, ClassLoader classLoader, MethodType type) {
		if(!(obj instanceof BaseConvertAOP)) {
			return;
		}
		LoadClassKey convertClass = metricMatcher.loadClass(type);
		if(convertClass == null) {
			return;
		}
		try {
			Object convertInstance = InterceptorInstanceLoader.load(convertClass.getKey(), convertClass.getCls(), classLoader);
			if(convertInstance instanceof IConvertMatcher) {
				((IConvertMatcher)convertInstance).setMetricMatcher(metricMatcher);
			}
			((BaseConvertAOP) obj).init(convertInstance, metricEventBus, metricMatcher);
		} catch (IllegalAccessException | InstantiationException | ClassNotFoundException
				| AgentPackageNotFoundException e) {
			log.warn(e, "加载convert异常:{}", convertClass);
		}
	}

	@Override
	public IConfigMatch match() {
		return metricMatcher.match();
	}

	@Override
	public LoadClassKey loadClass(MethodType type) {
		LoadClassKey convertClass = metricMatcher.loadClass(type);
		if(convertClass == null) {
			return null;
		}
		// 其他type因无需求，未实现对应的类，如果后续有需求，可增加实现类
		String convertCls = null;
		if(MethodType.STATIC.equals(type)) {
			convertCls = "cn.ecpark.tool.agent.core.aop.base.ConvertStatisticMethodAOP";// TODO 此处使用getClass试试会不会有类加载问题，如果可行，就换成类加载，避免类换路径时此处不自动更换，也无法通过类依赖查询
		} else if(MethodType.INSTANCE.equals(type)) {
			convertCls = "cn.ecpark.tool.agent.core.aop.base.ConvertMethodAOP";
		}
		return convertCls == null ? null : new LoadClassKey(convertCls, "ConvertAOP:" + convertClass.getCls());
	}

}
