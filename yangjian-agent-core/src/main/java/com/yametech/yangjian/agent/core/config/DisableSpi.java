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
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.common.CoreConstants;
import com.yametech.yangjian.agent.core.core.InstanceManage;

/**
 * 禁用spi，晚于其他IConfigLoader实例加载
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年10月6日 下午10:23:16
 */
public class DisableSpi implements IConfigLoader {
	private static ILogger log = LoggerFactory.getLogger(DisableSpi.class);
	
	@Override
	public void load(String arguments) throws Exception {
		InstanceManage.listSpiClass().stream().filter(spiCls -> {
			String enableConfig = Config.getKv("spi." + spiCls.getSimpleName());
			return enableConfig != null && !CoreConstants.CONFIG_KEY_ENABLE.equals(enableConfig);
		}).forEach(spiCls -> {
			InstanceManage.removeSpi(spiCls);
			log.info("disable SPI：{}", spiCls.getName());
		});
	}
	
	@Override
	public int weight() {
		return 0;
	}
	
}
