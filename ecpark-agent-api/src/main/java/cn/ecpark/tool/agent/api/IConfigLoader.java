package cn.ecpark.tool.agent.api;

import cn.ecpark.tool.agent.api.base.IWeight;
import cn.ecpark.tool.agent.api.base.SPI;

/**
 * 用于加载配置的接口，目前实现包含本地配置加载、远程配置加载，实例使用默认类加载器加载
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年12月20日 上午11:15:49
 */
public interface IConfigLoader extends IWeight, SPI {
	
	void load(String arguments) throws Exception;
	
}
