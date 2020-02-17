package cn.ecpark.tool.agent.plugin.okhttp;

import cn.ecpark.tool.agent.api.base.IContext;
import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.common.StringUtil;
import cn.ecpark.tool.agent.api.convert.IMethodConvert;
import cn.ecpark.tool.agent.plugin.okhttp.context.ContextConstants;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 转换httpclient调用事件
 * <p>
 * 支持版本：okhttp-3.x
 *
 * @author dengliming
 * @date 2019/11/22
 */
public class OkHttpClientConvert implements IMethodConvert {
	
	@Override
	public List<TimeEvent> convert(Object thisObj, long startTime, Object[] allArguments, Method method, Object ret,
			Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
        if (!(thisObj instanceof IContext)) {
            return null;
        }

        String requestUrl = (String) ((IContext) thisObj)._getAgentContext(ContextConstants.HTTP_REQUEST_URL_CONTEXT_KEY);
        if (StringUtil.isEmpty(requestUrl)) {
            return null;
        }
        TimeEvent event = get(startTime);
		event.setIdentify( StringUtil.filterUrlParams(requestUrl));
		return Arrays.asList(event);
    }
}
