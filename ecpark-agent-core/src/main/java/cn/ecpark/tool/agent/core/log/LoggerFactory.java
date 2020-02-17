package cn.ecpark.tool.agent.core.log;

import java.io.File;
import java.io.IOException;

import cn.ecpark.tool.agent.api.common.Constants;
import cn.ecpark.tool.agent.api.common.StringUtil;
import cn.ecpark.tool.agent.core.config.Config;
import cn.ecpark.tool.agent.core.log.impl.PatternLogger;
import cn.ecpark.tool.agent.core.util.AgentPath;
import cn.ecpark.tool.agent.core.util.OSUtil;

/**
 * @author zcn
 * @date: 2019-10-14
 * @description: logger factory
 **/
public class LoggerFactory {
	private static final String LOG_CONFIG_NAME = "log.properties";
	private static final String SKYWALKING_SERVICE_NAME_CONFIGKEY = "skywalking.agent.service_name";
    public static final LogOutput DEFAULT_OUTPUT = LogOutput.CONSOLE;
    public static final String DEFAULT_DIR = AgentPath.getPath().getAbsolutePath() + "\\logs";
    public static final LogLevel DEFAULT_LEVEL = LogLevel.DEBUG;
    public static final Long DEFAULT_MAX_FILE_SIZE = 1024 * 1024 * 30L;
    public static final Integer DEFAULT_MAX_FILE_NUM = 10;
    public static final String DEFAULT_PATTERN = "%timestamp[%level]-[%thread]-[%class.method]: %msg %throwable";

    /**
     * 初始化日志相关的配置，包含应用名称，应用名称会作为日志目录，所以必须提前初始化
     * @param arguments
     * @throws IOException
     */
    static {
		String configPath = AgentPath.getPath().getPath() + File.separator + "config" + File.separator + LOG_CONFIG_NAME;
		try {
			Config.addConfigProperties(configPath);
		} catch (IOException e) {
			System.err.println("init log exception:" + e.getMessage());
		}
		
		// 获取System中的应用名称配置Key，不存在时使用配置文件中的key
		String serviceName = System.getProperty(Constants.SYSTEM_PROPERTIES_PREFIX + Config.SERVICE_NAME.getKey());
		if(StringUtil.isEmpty(serviceName)) {// 此处对skywalking配置做自动读取，减少研发配置
			serviceName = System.getProperty(SKYWALKING_SERVICE_NAME_CONFIGKEY);
		}
		if(!StringUtil.isEmpty(serviceName)) {
			Config.SERVICE_NAME.setValueByKey(Config.SERVICE_NAME.getKey(), serviceName);
		}
		
		if(Config.getKv(Constants.LOG_DIR) == null) {
			// 设置日志目录
			if(OSUtil.isLinux()) {// 其他环境的日志目录/data/www/logs/ecpark-agent/应用名称/statistic.20191015.1.log
				String logDir = "/data/www/logs/cus-ecpark-agent";// TODO 修改为使用配置
				Config.setConfig(Constants.LOG_DIR, logDir);// 设置默认日志目录
			} else {// 研发环境的日志目录
				String logDir = AgentPath.getPath().getPath() + File.separator + "logs";
				Config.setConfig(Constants.LOG_DIR, logDir);// 设置默认日志目录
			}
		}
//		System.err.println("日志输出目录：" + Config.getKv(Constants.LOG_DIR));// 线上调试
    }
    
    public static ILogger getLogger(Class<?> clazz){
        return new PatternLogger(Config.getKv(Constants.LOG_PATTERN, DEFAULT_PATTERN), clazz);
    }
}
