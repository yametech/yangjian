package cn.ecpark.tool.agent.api.convert;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 注意：实现类不能同时实现SPI接口
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年12月21日 下午10:05:13
 */
public interface IMethodAsyncConvert extends IAsyncConvert, IConvertMatcher {
	
	/**
	 * 实例方法在调用结束后使用参数同步转换为临时对象
	 * @param thisObj	当前实例对象
	 * @param startTime	方法开始执行时间（毫秒）
	 * @param allArguments	所有参数
	 * @param method	方法定义
	 * @param ret	方法返回值
	 * @param t	方法抛出的异常（未抛出则为null）
	 * @param globalVar	本次调用中所有拦截器设置的上下文数据
	 * @return
	 */
	List<Object> convert(Object thisObj, long startTime, Object[] allArguments, Method method,
            Object ret, Throwable t, Map<Class<?>, Object> globalVar) throws Throwable;
	
}
