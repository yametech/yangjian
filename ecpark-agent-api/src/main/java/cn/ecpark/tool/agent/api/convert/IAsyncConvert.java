package cn.ecpark.tool.agent.api.convert;

import java.util.List;

import cn.ecpark.tool.agent.api.bean.TimeEvent;

/**
 * 注意：实现类不能同时实现SPI接口
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年12月21日 下午10:05:13
 */
public interface IAsyncConvert {
	/**
	 * 异步执行临时对象转换为 TimeEvent，主要用于直接转换为TimeEvent较耗时的场景，将转换步骤分为耗时较少的convert与耗时较多的convert
	 * 注意：此处需注意临时对象不包含可变数据，防止异步执行时内部数据变更导致转换失败
	 * @param eventBean
	 * @return
	 */
	List<TimeEvent> convert(Object eventBean);
	
}
