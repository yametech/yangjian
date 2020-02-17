package cn.ecpark.tool.agent.plugin.httpclient;

import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.common.StringUtil;
import cn.ecpark.tool.agent.api.convert.IMethodConvert;
import org.apache.http.HttpRequest;
import org.apache.http.RequestLine;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 转换httpclient调用事件
 * <p>
 * 支持版本：4.0.x-4.5.x
 *
 * @author dengliming
 * @date 2019/11/21
 */
public class CloseableHttpClientConvert implements IMethodConvert {
	
	@Override
	public List<TimeEvent> convert(Object thisObj, long startTime, Object[] allArguments, Method method, Object ret,
			Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
		if (!(allArguments[1] instanceof HttpRequest)) {
            return null;
        }
        HttpRequest request = (HttpRequest) allArguments[1];
        RequestLine requestLine = request.getRequestLine();
        if (requestLine == null) {
            return null;
        }
        String requestUrl = StringUtil.filterUrlParams(requestLine.getUri());
        if (StringUtil.isEmpty(requestUrl)) {
            return null;
        }
        TimeEvent event = get(startTime);
		event.setIdentify(requestUrl);
		return Arrays.asList(event);
	}
	
}
