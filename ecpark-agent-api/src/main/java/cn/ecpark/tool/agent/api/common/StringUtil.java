package cn.ecpark.tool.agent.api.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.convert.statistic.StatisticType;

/**
 * @author zcn
 * @date: 2019-10-16
 **/
public class StringUtil {
	private static final String DEFAULT_CHARSET = "UTF-8";

    public static boolean isEmpty(String str){
        return str == null || str.length() == 0;
    }

    public static boolean notEmpty(String str){
    	return !isEmpty(str);
    }
    
    public static String encode(String str) {
    	try {
			return URLEncoder.encode(str, DEFAULT_CHARSET);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
    }

    /**
     * 过滤请求url后面的参数
     *
     * @param url
     * @return
     */
    public static String filterUrlParams(String url) {
        if (notEmpty(url)) {
            int endIndex = url.indexOf('?');
            if (endIndex > 0) {
                return url.substring(0, endIndex);
            }
        }
        return url;
    }

    public static String encode(byte[] data) {
        try {
            return new String(data, DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            //ignore
        }
        return null;
    }
    
    public static String join(Object[] array) {
    	return join(array, ", ");
    }
	public static String join(Object[] array, String split) {
    	if(array == null) {
    		return "null";
    	}
    	StringBuilder builder = new StringBuilder("[");
    	for(Object o : array) {
    		builder.append(o).append(split);
    	}
    	if(array.length > 0) {
    		builder.delete(builder.length() - split.length(), builder.length());
    	}
    	builder.append(']');
    	return builder.toString();
    }
    
    public static void main(String[] args) {
		System.err.println(join(null));
		System.err.println(join(new Object[0]));
		System.err.println(join(new String[] {"111", "2222", "333"}));
		TimeEvent event = new TimeEvent(new StatisticType[] {StatisticType.QPS, StatisticType.RT});
		event.setEventTime(123L);
		event.setIdentify("identify");
		event.setNumber(100);
		event.setType("type");
		event.setUseTime(100000);
		System.err.println(event);
		
	}
}
