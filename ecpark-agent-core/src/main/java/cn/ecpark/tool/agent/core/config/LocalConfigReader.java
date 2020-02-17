package cn.ecpark.tool.agent.core.config;

import java.io.File;
import java.util.Properties;

import cn.ecpark.tool.agent.api.IConfigLoader;
import cn.ecpark.tool.agent.api.common.Constants;
import cn.ecpark.tool.agent.api.common.StringUtil;
import cn.ecpark.tool.agent.core.log.ILogger;
import cn.ecpark.tool.agent.core.log.LoggerFactory;
import cn.ecpark.tool.agent.core.util.AgentPath;

/**
 * 加载本地配置，配置来源包含：jvm启动参数、指定路径配置文件、默认配置文件
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年10月6日 下午10:23:16
 */
public class LocalConfigReader implements IConfigLoader {
	private static ILogger log = LoggerFactory.getLogger(LocalConfigReader.class);
	private static final String DEFAULT_CONFIG_FILE = "agent.properties";
//	private static final String DEFAULT_CONFIG_PATH = "config" + File.separator + DEFAULT_CONFIG_FILE;// 默认本地配置路径
	
	@Override
	public void load(String arguments) throws Exception {
		readLocalConfigFile();
		readSystemArguments();
		readArguments(arguments);
	}
	
	/**
	 * 读取启动参数中的配置，如：-javaagent:...ecpark-agent.jar=arg=123,arg2=qqq，则此处的arguments为arg=123,arg2=qqq
	 * @param arguments
	 */
	private void readArguments(String arguments) {
		if(arguments == null || arguments.trim().length() == 0) {
			return;
		}
		for(String arg : arguments.split(",")) {
			String[] kv = arg.trim().split("=", 2);
			if(kv.length == 0) {
				continue;
			}
			String key = kv[0].trim();
			String value = "";
			if(kv.length == 2) {
				value = kv[1].trim();
			}
			Config.setConfig(key, value);
		}
	}
	
	/**
	 * 读取启动参数中的配置，如：-DMonitorAgent.service.name=tttt
	 * @param arguments
	 */
	private void readSystemArguments() {
		Properties properties = System.getProperties();
		properties.keySet().stream()
				.filter( key -> key.toString().startsWith(Constants.SYSTEM_PROPERTIES_PREFIX))
				.forEach(key -> Config.setConfig(key.toString().substring(Constants.SYSTEM_PROPERTIES_PREFIX.length()), properties.get(key).toString()));
	}
	
	/**
	 * 读取本地文件配置
	 * @throws Exception
	 */
	private void readLocalConfigFile() throws Exception {
		String configPath = System.getProperty(Constants.CONFIG_PATH);
		if(StringUtil.isEmpty(configPath)) {
			configPath = AgentPath.getPath().getPath() + File.separator + "config" + File.separator + DEFAULT_CONFIG_FILE;
//			String basePath = AgentPath.getFile().getPath();
//			if(basePath.endsWith("target\\classes")) {// 兼容研发环境
//				configPath = basePath + File.separator + DEFAULT_CONFIG_FILE;
//			} else {
//				configPath = basePath + ".." + File.separator + DEFAULT_CONFIG_PATH;
//			}
		}
		Config.addConfigProperties(configPath);
		log.info("加载配置文件路径：" + configPath);
	}
	
	@Override
	public int weight() {
		return 99;
	}
	
}
