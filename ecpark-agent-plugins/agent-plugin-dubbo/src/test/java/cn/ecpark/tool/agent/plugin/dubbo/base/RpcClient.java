package cn.ecpark.tool.agent.plugin.dubbo.base;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;

import cn.ecpark.tool.agent.plugin.dubbo.Utils;

public class RpcClient extends Rpc<RpcClient> {
//	private static Logger log = Logger.getLogger(RpcClient.class);
//	private static final int DEFAULT_TIMEOUT_MILLIS = 15000;
	
	private static RpcClient server = new RpcClient();
	
//	private Map<Class<?>, Map<String, Object>> cachedService = new HashMap<Class<?>, Map<String, Object>>();
	private Map<String, Object> cachedService = new HashMap<String, Object>();
	
	private int timeoutMillis = 0;
	private boolean isDirectConnect = false;// 是否是直连服务（不走注册中心）
	private boolean async = false;
	
	private RpcClient() {}
	
	public static RpcClient instance() {
		return server;
	}
	
	public RpcClient directConnect() {
		isDirectConnect = true;
		return server;
	}
	
	public RpcClient timeout(int millis) {
		timeoutMillis = millis;
		return server;
	}
	
	/**
	 * 是否启用异步调用
	 * @param async
	 * @return
	 */
	public RpcClient async(boolean async) {
		this.async = async;
		return server;
	}
	
	/**
	 * 获取接口代理实例，此处会缓存实例(首次调用会很慢)
	 * 
	 * 官方提供的方式：
	 * ReferenceConfigCache cache = ReferenceConfigCache.getCache();
	 * cache.get⽅法中会缓存 Reference对象，并且调⽤ReferenceConfig.get⽅法启动ReferenceConfig
	 * XxxService xxxService = cache.get(reference);
	 * // 注意！ Cache会持有ReferenceConfig，不要在外部再调⽤ReferenceConfig的destroy⽅法，导致Cache内的ReferenceConfig失效！
	 * // 使⽤xxxService对象
	 * xxxService.sayHello();
	 * 
	 * @param cls
	 * @param version
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> T get(Class<T> cls, String version, boolean async) {
		String cacheKey = cls.getSimpleName()+"||"+version+"||"+async;
		if(cachedService.containsKey(cacheKey)) {
			return (T) cachedService.get(cacheKey);
		}
		Utils.checkArgument(cls == null, "接口不能为null");
		Utils.checkArgument(version == null, "版本号不能为null");
		Utils.checkStatus(getApplication().getName() == null, "必须设置应用名称");
		Utils.checkStatus(getRegistrys().size() == 0, "未配置注册地址");
		synchronized (cls) {
			if(cachedService.containsKey(cacheKey)) {
				return (T) cachedService.get(cacheKey);
			}
			ReferenceConfig<T> reference = new ReferenceConfig<T>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
			reference.setApplication(getApplication());
			reference.setInterface(cls);
			reference.setVersion(version);
			if(async) {
				reference.setAsync(async);
			}
			if(isDirectConnect) {
				StringBuilder addresses = new StringBuilder();
				for(RegistryConfig config : getRegistrys()) {
					addresses.append(config.getAddress() + ";");
				}
				if(addresses.length() > 0) {
					addresses.deleteCharAt(addresses.length() - 1);
				}
				reference.setUrl(addresses.toString());
			} else {
				reference.setRegistries(getRegistrys());
				reference.setCheck(false);
			}
			if(timeoutMillis > 0) {
				reference.setTimeout(timeoutMillis);
			}
			
			T service = reference.get();
			cachedService.put(cacheKey, service);// 缓存服务类
			return service;
		}

	}
	
	public static <T> T getService(Class<T> cls) {
		return getService(cls, server.getVersion());
	}
	
	public static <T> T getService(Class<T> cls, String version) {
		return getService(cls, version, server.async);
	}
	
	public static <T> T getService(Class<T> cls, boolean async) {
		return getService(cls, server.getVersion(), async);
	}
	/**
	 * 获取服务实例
	 * @param cls	需要获取的服务接口
	 * @param version	服务版本
	 * @param async	是否异步调用
	 * @return
	 */
	public static <T> T getService(Class<T> cls, String version, boolean async) {
		return server.get(cls, version, async);
	}
	
}
