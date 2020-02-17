package cn.ecpark.tool.agent.api.convert;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import cn.ecpark.tool.agent.api.bean.TimeEvent;

/**
 * 注意：实现类不能同时实现SPI接口
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年12月21日 下午10:05:06
 */
public interface IStatisticMethodConvert extends IConvertMatcher {
	
	/**
	 * 实例方法在调用结束后使用参数同步转换为TimeEvent对象，如果转换过程比较耗时，则使用IMethodAsyncConvert
	 * @param startTime	方法开始执行时间（毫秒）
	 * @param allArguments	所有参数
	 * @param method	方法定义
	 * @param ret	方法返回值
	 * @param t	方法抛出的异常（未抛出则为null）
	 * @param globalVar	本次调用中所有拦截器设置的上下文数据
	 * @return
	 */
	List<TimeEvent> convert(long startTime, Object[] allArguments, Method method,
            Object ret, Throwable t, Map<Class<?>, Object> globalVar) throws Throwable;
	
}
