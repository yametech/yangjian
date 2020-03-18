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
