package cn.ecpark.tool.agent.plugin.spring;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.web.method.support.InvocableHandlerMethod;

import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.common.MethodUtil;
import cn.ecpark.tool.agent.api.convert.IMethodConvert;

/**
 * 转换spring controller事件
 *
 * @author liuzhao
 * @Description
 * @date 2019年10月9日 下午3:43:15
 */
public class ControllerConvert implements IMethodConvert {

	@Override
	public List<TimeEvent> convert( Object thisObj, long startTime, Object[] allArguments, Method method, Object ret,
			Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
//		System.err.println(thisObj.getClass().getClassLoader());
//		System.err.println(this.getClass().getClassLoader());
//		System.err.println(Thread.currentThread().getContextClassLoader());
		
		if (!(thisObj instanceof InvocableHandlerMethod)) {
            return null;
        }
        InvocableHandlerMethod handlerMethod = (InvocableHandlerMethod) thisObj;
        TimeEvent event = get(startTime);
		event.setIdentify(MethodUtil.getCacheMethodId(handlerMethod.getMethod()));
		return Arrays.asList(event);
    }
}
