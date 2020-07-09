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
package com.yametech.yangjian.agent.core.config;

import com.yametech.yangjian.agent.api.IConfigLoader;
import com.yametech.yangjian.agent.api.IMetricMatcher;
import com.yametech.yangjian.agent.api.common.Config;
import com.yametech.yangjian.agent.api.common.InstanceManage;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.api.pool.IPoolMonitorMatcher;
import com.yametech.yangjian.agent.api.trace.ITraceMatcher;
import com.yametech.yangjian.agent.core.common.CoreConstants;
import com.yametech.yangjian.agent.core.pool.PoolMonitorSchedule;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 禁用spi，晚于其他IConfigLoader实例加载
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年10月6日 下午10:23:16
 */
public class DisableSpi implements IConfigLoader {
	private static ILogger log = LoggerFactory.getLogger(DisableSpi.class);
	private static final Set<String> NOT_ALLOW_DISABLE_SPI = new HashSet<>(Arrays.asList(LocalConfigLoader.class.getSimpleName(),
			DisableSpi.class.getSimpleName(), PoolMonitorSchedule.class.getSimpleName()));// 不可以禁用的插件
	
	@Override
	public void load(String arguments) throws Exception {
		InstanceManage.listSpiClass().stream().filter(spiCls -> {
			String configSuffix = spiCls.getSimpleName();
			boolean notAllow = NOT_ALLOW_DISABLE_SPI.contains(configSuffix);
			if(notAllow) {// 不可禁用
				return false;
			}
			String enableConfig = Config.getKv("spi." + configSuffix);
			if(CoreConstants.CONFIG_KEY_ENABLE.equals(enableConfig)) {// 配置了启用
				return false;
			}
			String defaultConfig = defaultConfig(spiCls);
			return defaultConfig != null && !CoreConstants.CONFIG_KEY_ENABLE.equals(defaultConfig);
		}).forEach(spiCls -> {
			InstanceManage.removeSpi(spiCls);
			log.info("disable SPI：{}", spiCls.getName());
		});
	}

	private String defaultConfig(Class<?> spiCls) {
		if(IMetricMatcher.class.isAssignableFrom(spiCls)) {
			return Config.getKv("spi.Metric");
		} else if(IPoolMonitorMatcher.class.isAssignableFrom(spiCls)) {
			return Config.getKv("spi.PoolMonitor");
		} else if(ITraceMatcher.class.isAssignableFrom(spiCls)) {
			return Config.getKv("spi.Trace");
		}
		return null;
	}

	@Override
	public int weight() {
		return 0;
	}
	
}
