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

package com.yametech.yangjian.agent.tests.tool;

import com.yametech.yangjian.agent.api.common.Config;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.core.YMAgent;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author dengliming
 * @date 2020/9/21
 */
public abstract class AbstractAgentTest {

    protected static MockTracerServer mockTracerServer;
    protected static MockMetricServer mockMetricServer;

    @BeforeClass
    public static void setup() {
        mockTracerServer = new MockTracerServer();
        mockTracerServer.start();
        mockMetricServer = new MockMetricServer();
        mockMetricServer.start();
        System.setProperty("log.config.path", "/data/www/soft/ecpark-agent/config/log.properties");
        System.setProperty(Constants.SYSTEM_PROPERTIES_PREFIX + Config.SERVICE_NAME.getKey(), "test");
        System.setProperty(Constants.CONFIG_PATH, "/data/www/soft/ecpark-agent/config/agent.properties");
        System.setProperty(Constants.EXTEND_PLUGINS_DIR, "/data/www/soft/ecpark-agent/plugins");
        YMAgent.premain(String.join(",",
                "report.spanListener=http-span",
                "report.http-statistic.url=http://localhost:9412/api/metric/report",
                "report.statistic=http-statistic",
                "spi.Trace=enable",
                "trace.sample.strategy.mq-publish=always",
                "trace.sample.strategy.mq-consume=always",
                "trace.sample.strategy.dubbo-client=always",
                "trace.sample.strategy.dubbo-server=always",
                "trace.sample.strategy.http-server=always",
                "trace.sample.strategy.http-client=always",
                "trace.sample.strategy.redis=always",
                "trace.sample.strategy.mysql=always",
                "trace.sample.strategy.mongo=always",
                "metricOutput.interval.consume.metric=1",
                "metricOutput.interval.consume.trace=1",
                "metric.period.second=1",
                // all test redis key use `TS` as prefix
                "redis.key.rule=TS"
                ), ByteBuddyAgent.install());
    }

    @AfterClass
    public static void afterClass() {
        mockTracerServer.stop();
        mockMetricServer.stop();
    }

    @Before
    public void before() {
        mockTracerServer.clear();
        mockMetricServer.clear();
    }
}
