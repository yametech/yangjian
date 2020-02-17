package cn.ecpark.tool.agent.plugin.method;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.common.MethodUtil;
import cn.ecpark.tool.agent.api.convert.IStatisticMethodConvert;

/**
 * 转换静态方法调用RT
 *
 * @author liuzhao
 * @Description
 * @date 2019年10月9日 下午3:43:15
 */
public class StatisticMethodConvert implements IStatisticMethodConvert {

	@Override
	public List<TimeEvent> convert(long startTime, Object[] allArguments, Method method, Object ret,
			Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
        TimeEvent event = get(startTime);
		event.setIdentify(MethodUtil.getCacheMethodId(method));
		return Arrays.asList(event);
    }
}
