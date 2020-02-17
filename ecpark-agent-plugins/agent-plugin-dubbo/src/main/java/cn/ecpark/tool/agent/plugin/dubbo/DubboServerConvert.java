package cn.ecpark.tool.agent.plugin.dubbo;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.common.ClassUtil;
import cn.ecpark.tool.agent.api.common.MethodUtil;
import cn.ecpark.tool.agent.api.convert.IMethodConvert;

/**
 * 将dubbo服务端调用，转换成实际调用的接口
 * 支持版本：
 * alibaba：dubbo-2.4.10、dubbo-2.5.3、dubbo-2.5.4、dubbo-2.5.5、dubbo-2.5.6、dubbo-2.5.7、dubbo-2.5.10、dubbo-2.6.0、dubbo-2.6.1、dubbo-2.6.2、dubbo-2.6.3、dubbo-2.6.4、dubbo-2.6.5、dubbo-2.6.6、dubbo-2.6.7、dubbo-2.8.3、dubbo-2.8.4
 * apache：dubbo-2.7.0、dubbo-2.7.1、dubbo-2.7.2、dubbo-2.7.3、dubbo-2.7.4
 *
 * @author liuzhao
 * @Description
 * @date 2019年10月9日 下午3:43:15
 */
public class DubboServerConvert implements IMethodConvert {

	@Override
	public List<TimeEvent> convert(Object thisObj, long startTime, Object[] allArguments, Method method, Object ret,
			Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
        if (allArguments[0] == null || allArguments[1] == null || !(allArguments[2] instanceof Class<?>[])) {
            return null;
        }
        Class<?> instanceCls = allArguments[0].getClass();
        String methodName = (String) allArguments[1];
        Class<?>[] parameterTypes = (Class<?>[]) allArguments[2];
        TimeEvent event = get(startTime);
		event.setIdentify(MethodUtil.getCacheMethodId(ClassUtil.getOriginalClass(instanceCls), methodName, parameterTypes));
		return Arrays.asList(event);
    }
}
