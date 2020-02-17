package cn.ecpark.tool.agent.plugin.dubbo;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.common.MethodUtil;
import cn.ecpark.tool.agent.api.convert.IMethodConvert;

/**
 * 将dubbo消费端注册的接口调用，转换成实际调用的接口，dubbo使用了代理所以需要转换
 * 支持版本：
 * 		alibaba：dubbo-2.4.10、dubbo-2.5.3、dubbo-2.5.4、dubbo-2.5.5、dubbo-2.5.6、dubbo-2.5.7、dubbo-2.5.10、dubbo-2.6.0、dubbo-2.6.1、dubbo-2.6.2、dubbo-2.6.3、dubbo-2.6.4、dubbo-2.6.5、dubbo-2.6.6、dubbo-2.6.7、dubbo-2.8.3、dubbo-2.8.4
 * 		apache：dubbo-2.7.0、dubbo-2.7.1、dubbo-2.7.2、dubbo-2.7.3、dubbo-2.7.4
 * @Description
 *
 * @author liuzhao
 * @date 2019年10月9日 下午3:43:15
 */
public class DubboClientConvert implements IMethodConvert {

	@Override
	public List<TimeEvent> convert(Object thisObj, long startTime, Object[] allArguments, 
			Method method, Object ret, Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
		if (allArguments[1] == null) {
			return null;
		}
		TimeEvent event = get(startTime);
		event.setIdentify(MethodUtil.getCacheMethodId((Method) allArguments[1]));
		return Arrays.asList(event);
	}
}
