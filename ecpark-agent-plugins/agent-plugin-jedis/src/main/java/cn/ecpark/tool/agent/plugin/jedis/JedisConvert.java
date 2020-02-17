package cn.ecpark.tool.agent.plugin.jedis;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.ecpark.tool.agent.api.IMetricMatcher;
import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.common.StringUtil;
import cn.ecpark.tool.agent.api.convert.IMethodAsyncConvert;
import cn.ecpark.tool.agent.plugin.jedis.bean.RedisKeyBean;

/**
 * 转换jedis调用事件
 *
 * @author dengliming
 * @date 2019/12/4
 */
public class JedisConvert implements IMethodAsyncConvert {
    private JedisMatcher jedisMatcher;
    
    @Override
    public void setMetricMatcher(IMetricMatcher metricMatcher) {
    	jedisMatcher = (JedisMatcher) metricMatcher;
    }
    
    @Override
    public List<Object> convert(Object thisObj, long startTime, Object[] allArguments, Method method, Object ret,
    		Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
        if (allArguments == null || allArguments.length == 0) {
            return null;
        }
        String key = null;
        if (allArguments[0] instanceof String) {
            key = (String) allArguments[0];
        } else if (allArguments[0] instanceof byte[]) {
            key = StringUtil.encode((byte[]) allArguments[0]);
        }
        long now = System.currentTimeMillis();
        return Arrays.asList(new RedisKeyBean(Arrays.asList(key), now, now - startTime));
    }
    
    @Override
    public List<TimeEvent> convert(Object eventBean) {
    	RedisKeyBean redisKeyBean = (RedisKeyBean) eventBean;
        return jedisMatcher.getMatchKeyTimeEvents(redisKeyBean);
    }
    
}
