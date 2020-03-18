/*
 * Copyright 2020 yametech.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yametech.yangjian.agent.plugin.dubbo.base;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.dubbo.config.MonitorConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ServiceConfig;

import com.yametech.yangjian.agent.plugin.dubbo.Utils;

public class RpcServer extends Rpc<RpcServer> {
	
	private static RpcServer server = new RpcServer();
	private static final String DEFAULT_PROTOCOL_NAME = "dubbo";
	
	private List<ProtocolConfig> protocols = new ArrayList<ProtocolConfig>();// 服务提供者协议集合
	private List<ServiceConfig<?>> services = new ArrayList<ServiceConfig<?>>();
	
	private RpcServer() {}
	
	public static RpcServer instance() {
		return server;
	}
	
	/**
	 * 添加协议
	 * @param port
	 * @return
	 */
	public RpcServer addProtocol(int port) {
		return addProtocol(null, port, null);
	}
	/**
	 * 
	 * @param transporter	传输类型，默认netty，也可使用mina、grizzly，但需要有对应的jar
	 * @param port
	 * @return
	 */
	public RpcServer addProtocol(int port, int maxThreadNum) {
		return addProtocol(null, port, maxThreadNum);
	}
	public RpcServer addProtocol(String transporter, int port, Integer maxThreadNum) {
		ProtocolConfig protocol = new ProtocolConfig();
		protocol.setName(DEFAULT_PROTOCOL_NAME);
		protocol.setPort(port);
		if(maxThreadNum != null) {
			protocol.setThreadpool("cached");// 默认为fixed，core线程个数即为最大个数，不会按需创建线程，不会伸缩
			protocol.setThreads(maxThreadNum);
		}
		if(transporter != null && !transporter.trim().equals("")) {
			protocol.setTransporter(transporter);
		}
		protocols.add(protocol);
		return server;
	}
	
	/**
	 * 开启服务类
	 * @param cls
	 * @param instance
	 * @param version
	 * @return
	 */
	public <T> RpcServer start(Class<T> cls, T instance) {
		return start(cls, instance, server.getVersion());
	}
	public <T> RpcServer start(Class<T> cls, T instance, String version) {
		Utils.checkArgument(cls == null, "接口不能为null");
		Utils.checkArgument(instance == null, "服务实现类不能为null");
		Utils.checkStatus(getApplication().getName() == null, "必须设置应用名称");
		// 服务提供者暴露服务配置
		ServiceConfig<T> config = new ServiceConfig<T>(); // 此实例很重，封装了与注册中心的连接，请自行缓存，否则可能造成内存和连接泄漏
		config.setApplication(getApplication());
		config.setRegistries(getRegistrys());
		config.setProtocols(protocols);
		config.setInterface(cls);
		config.setRef(instance);
		config.setVersion(version);
		// 添加监控配置
		MonitorConfig monitorConfig = new MonitorConfig();
		monitorConfig.setProtocol("registry");
		config.setMonitor(monitorConfig);
		
		config.export();
		services.add(config);
		//log.info(instance.getClass().getSimpleName() + "服务已开启");
		return server;
	}
	
	/**
	 * 判断是否所有的服务都停止了，该方法暂时不要用
	 * @return
	 */
	public boolean stop() {
		for(ProtocolConfig config : protocols) {
			try {
//				config.destory();
				config.destroy();
			} catch (Exception e) {
				//log.warn("停止服务异常", e);
			}
		}
		return true;
	}
	
}
