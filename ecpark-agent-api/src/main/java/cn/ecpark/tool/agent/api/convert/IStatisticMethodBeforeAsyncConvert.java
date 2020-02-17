package cn.ecpark.tool.agent.api.convert;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 注意：实现类不能同时实现SPI接口
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年12月21日 下午10:05:13
 */
public interface IStatisticMethodBeforeAsyncConvert extends IAsyncConvert, IConvertMatcher {
	
	/**
	 * 实例方法在调用前使用参数同步转换为临时对象
	 * @param allArguments	所有参数
	 * @param method	方法定义
	 * @return
	 */
	List<Object> convert(Object[] allArguments, Method method) throws Throwable;
	
}
