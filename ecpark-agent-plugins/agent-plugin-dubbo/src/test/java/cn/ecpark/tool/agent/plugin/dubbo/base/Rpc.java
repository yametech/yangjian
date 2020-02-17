package cn.ecpark.tool.agent.plugin.dubbo.base;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;

@SuppressWarnings("unchecked")
public class Rpc<T extends Rpc<?>> {
	private static final String DEFAULT_VERSION = "1.0.0";
	
	private List<RegistryConfig> registrys = new ArrayList<RegistryConfig>();// 注册中心集合
	private ApplicationConfig application = new ApplicationConfig();
	private String version = DEFAULT_VERSION;
	
	protected Rpc() {}
	
	/**
	 * 设置应用标识
	 * @param name
	 * @return
	 */
	public T appName(String name) {
		application.setName(name);
		return (T) this;
	}
	
	public T version(String version) {
		this.version = version;
		return (T) this;
	}
	
	/**
	 * 添加注册服务地址
	 * @param ipPort
	 * @return
	 */
	public T addRegistry(String ipPort) {
		return addRegistry(ipPort, null, null);
	}
	public T addRegistry(String ipPort, String username, String password) {
		RegistryConfig registry = new RegistryConfig();
		registry.setAddress(ipPort);
		registry.setUsername(username);
		registry.setPassword(password);
		registrys.add(registry);
		return (T) this;
	}
	
	protected ApplicationConfig getApplication() {
		return application;
	}
	
	protected List<RegistryConfig> getRegistrys() {
		return registrys;
	}
	
	protected String getVersion() {
		return version;
	}
}
