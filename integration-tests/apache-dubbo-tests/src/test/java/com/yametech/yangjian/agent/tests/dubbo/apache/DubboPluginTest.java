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

package com.yametech.yangjian.agent.tests.dubbo.apache;

import com.yametech.yangjian.agent.tests.tool.AbstractAgentTest;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.junit.Test;
import zipkin2.Span;

import java.time.Duration;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author dengliming
 * @date 2020/9/22
 */
public class DubboPluginTest extends AbstractAgentTest {

    private DubboTestApi dubboTestApi;

    @Test
    public void testHeart() {
        DubboTestApi dubboTestApi = getDubboService();
        dubboTestApi.heart();
        List<Span> spans = mockTracerServer.waitForSpans(2, Duration.ofSeconds(5).toMillis());
        assertNotNull(spans);
        assertEquals(2, spans.size());
        System.out.println(spans.get(1));
        assertEquals(spans.get(0).traceId(), spans.get(1).parentId());
        assertEquals("com.yametech.yangjian.agent.tests.dubbo.apache.DubboTestApi.heart()", spans.get(0).name());
        assertEquals(Span.Kind.CLIENT, spans.get(0).kind());
        assertEquals("com.yametech.yangjian.agent.tests.dubbo.apache.DubboTestApiImpl.heart()", spans.get(1).name());
        assertEquals(Span.Kind.SERVER, spans.get(1).kind());
    }

    @Test
    public void testHello() {
        DubboTestApi dubboTestApi = getDubboService();
        dubboTestApi.hello("hi");
        List<Span> spans = mockTracerServer.waitForSpans(2, Duration.ofSeconds(5).toMillis());
        assertNotNull(spans);
        assertEquals(2, spans.size());
        assertEquals(spans.get(0).traceId(), spans.get(1).parentId());
        assertEquals("com.yametech.yangjian.agent.tests.dubbo.apache.DubboTestApi.hello(String)", spans.get(0).name());
        assertEquals(Span.Kind.CLIENT, spans.get(0).kind());
        assertEquals("com.yametech.yangjian.agent.tests.dubbo.apache.DubboTestApiImpl.hello(String)", spans.get(1).name());
        assertEquals(Span.Kind.SERVER, spans.get(1).kind());
    }

    public DubboTestApi getDubboService() {
        if (dubboTestApi == null) {
            dubboTestApi = buildDubboTestApi();
        }
        return dubboTestApi;
    }

    private static ReferenceConfig<DubboTestApi> referenceConfig;

    private static ServiceConfig<DubboTestApi> serviceConfig;

    private DubboTestApi buildDubboTestApi() {
        ApplicationConfig providerAppConfig = new ApplicationConfig();
        providerAppConfig.setName("dubbo-demo");

        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName("dubbo");
        protocolConfig.setPort(getPort());
        protocolConfig.setThreads(10);

        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("N/A");

        serviceConfig = new ServiceConfig<>();
        serviceConfig.setApplication(providerAppConfig);
        serviceConfig.setProtocol(protocolConfig);
        serviceConfig.setInterface(DubboTestApi.class);
        serviceConfig.setRef(new DubboTestApiImpl());
        serviceConfig.setRegistry(registryConfig);
        serviceConfig.export();

        referenceConfig = new ReferenceConfig<>();
        referenceConfig.setApplication(providerAppConfig);
        referenceConfig.setInterface(DubboTestApi.class);
        referenceConfig.setUrl("dubbo://localhost:" + getPort());
        referenceConfig.setTimeout(3000);

        return referenceConfig.get();
    }

    private int getPort() {
        return 20901;
    }
}
