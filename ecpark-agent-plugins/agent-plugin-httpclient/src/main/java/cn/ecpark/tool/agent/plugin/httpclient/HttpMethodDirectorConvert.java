package cn.ecpark.tool.agent.plugin.httpclient;

import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.common.StringUtil;
import cn.ecpark.tool.agent.api.convert.IMethodConvert;
import org.apache.commons.httpclient.HttpMethod;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 转换httpclient调用事件
 * 支持版本：httpclient-3、httpclient-3.1
 *
 * @author dengliming
 * @date 2019/11/21
 */
public class HttpMethodDirectorConvert implements IMethodConvert {
    
    @Override
    public List<TimeEvent> convert( Object thisObj, long startTime, Object[] allArguments, Method method, Object ret,
    		Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
    	HttpMethod httpMethod = (HttpMethod) allArguments[0];
    	String requestUrl = StringUtil.filterUrlParams(httpMethod.getURI().toString());
    	if (StringUtil.isEmpty(requestUrl)) {
    	    return null;
        }
        // 过滤url上的查询参数
        TimeEvent event = get(startTime);
		event.setIdentify(requestUrl);
		return Arrays.asList(event);
    }
    
}
