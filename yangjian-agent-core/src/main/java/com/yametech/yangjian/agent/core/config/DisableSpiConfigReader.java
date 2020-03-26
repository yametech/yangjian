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

import java.util.List;
import java.util.stream.Collectors;

import com.yametech.yangjian.agent.api.IConfigLoader;
import com.yametech.yangjian.agent.api.base.SPI;
import com.yametech.yangjian.agent.core.core.InstanceManage;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;

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
			InstanceManage.removeSpi(spi);
			log.info("disable SPI：{}", spi.getClass().getName());
		});
	}
	
	@Override
	public int weight() {
		return 0;
	}
	
}
