package cn.ecpark.tool.agent.core.config;

import java.util.List;
import java.util.stream.Collectors;

import cn.ecpark.tool.agent.api.IConfigLoader;
import cn.ecpark.tool.agent.api.base.SPI;
import cn.ecpark.tool.agent.core.core.InstanceManage;
import cn.ecpark.tool.agent.core.log.ILogger;
import cn.ecpark.tool.agent.core.log.LoggerFactory;

/**
 * 晚于其他配置加载
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年10月6日 下午10:23:16
 */
public class DisableSpiConfigReader implements IConfigLoader {
	private static ILogger log = LoggerFactory.getLogger(DisableSpiConfigReader.class);
	
	@Override
	public void load(String arguments) throws Exception {
		List<SPI> disableSpi = InstanceManage.getSpis().stream().filter(spi -> {
			String enableConfig = Config.getKv("spi." + spi.getClass().getSimpleName());
			return enableConfig != null && !CoreConstants.CONFIG_KEY_ENABLE.equals(enableConfig);
		}).collect(Collectors.toList());
		disableSpi.forEach(spi -> {
			InstanceManage.getSpis().remove(spi);
			log.info("disable SPI：{}", spi.getClass().getName());
		});
	}
	
	@Override
	public int weight() {
		return 0;
	}
	
}
