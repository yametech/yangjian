package cn.ecpark.tool.agent.plugin.hikaricp;

import cn.ecpark.tool.agent.api.bean.TimeEvent;
import cn.ecpark.tool.agent.api.common.Constants;
import cn.ecpark.tool.agent.api.convert.IMethodConvert;
import cn.ecpark.tool.agent.util.Utils;
import com.zaxxer.hikari.HikariDataSource;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author dengliming
 * @date 2019/12/22
 */
public class HikariConnectionConvert implements IMethodConvert {

    @Override
    public List<TimeEvent> convert(Object thisObj, long startTime, Object[] allArguments, Method method,
                                   Object ret, Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
        HikariDataSource hikariDataSource = (HikariDataSource) thisObj;
        String jdbcUrl = hikariDataSource.getJdbcUrl();
        TimeEvent event = get(startTime);
        event.setIdentify(Utils.parseJdbcUrl(jdbcUrl) + Constants.IDENTIFY_SEPARATOR + Constants.DbOperation.GET_CONNECTION);
        return Arrays.asList(event);
    }
}
