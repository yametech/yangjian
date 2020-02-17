package cn.ecpark.tool.agent.plugin.method;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.common.MethodUtil;
import cn.ecpark.tool.agent.api.convert.IMethodConvert;

/**
 * 转换实例方法调用RT
 *
 * @author liuzhao
 * @Description
 * @date 2019年10月9日 下午3:43:15
 */
public class InstanceMethodConvert implements IMethodConvert {

	@Override
	public List<TimeEvent> convert(Object thisObj, long startTime, Object[] allArguments, Method method, Object ret,
			Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
        TimeEvent event = get(startTime);
		event.setIdentify(MethodUtil.getCacheMethodId(method));
		return Arrays.asList(event);
    }
}
