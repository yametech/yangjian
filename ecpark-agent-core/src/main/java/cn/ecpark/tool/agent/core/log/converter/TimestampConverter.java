package cn.ecpark.tool.agent.core.log.converter;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.ecpark.tool.agent.core.log.Converter;
import cn.ecpark.tool.agent.core.log.LogEvent;

/**2019-10-24 22:20:39.707[INFO]-[main]-[c.z.h.HikariDataSource.<init>(80)]: HikariPool-1 - Starting...
 * @author zcn
 * @date: 2019-10-14
 **/
public class TimestampConverter implements Converter {

    @Override
    public String convert(LogEvent event) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }
}
