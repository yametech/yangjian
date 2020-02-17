package cn.ecpark.tool.agent.api.convert;

import java.lang.reflect.Method;
import java.util.List;

import cn.ecpark.tool.agent.api.bean.TimeEvent;

/**
 * 注意：实现类不能同时实现SPI接口
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年12月21日 下午10:05:06
 */
public interface IStatisticMethodBeforeConvert extends IConvertMatcher {
	
	/**
	 * 实例方法在调用前使用参数同步转换为TimeEvent对象，如果转换过程比较耗时，则使用IMethodBeforeAsyncConvert
	 * @param allArguments	所有参数
	 * @param method	方法定义
	 * @return
	 */
	List<TimeEvent> convert(Object[] allArguments, Method method) throws Throwable;
	
}
