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

package com.yametech.yangjian.agent.tests.dubbo.alibaba;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.yametech.yangjian.agent.tests.tool.AbstractAgentTest;
import com.yametech.yangjian.agent.tests.tool.bean.EventMetric;
import org.junit.BeforeClass;
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
public class AlibabaDubboPluginTest extends AbstractAgentTest {

    private static DubboTestApi dubboTestApi;
    private static GenericService genericService;

    @BeforeClass
    public static void init() {
        initDubboConfig();
        dubboTestApi = referenceConfig.get();
        genericService = genericReferenceConfig.get();
    }

    @Test
    public void testHeart() {
        dubboTestApi.heart();
        List<Span> spans = mockTracerServer.waitForSpans(2, Duration.ofSeconds(5).toMillis());
        assertNotNull(spans);
        assertEquals(2, spans.size());
        System.out.println(spans.get(1));
        assertEquals(spans.get(0).traceId(), spans.get(1).parentId());
        assertEquals("com.yametech.yangjian.agent.tests.dubbo.alibaba.DubboTestApi.heart()", spans.get(0).name());
        assertEquals(Span.Kind.CLIENT, spans.get(0).kind());
        assertEquals("com.yametech.yangjian.agent.tests.dubbo.alibaba.DubboTestApiImpl.heart()", spans.get(1).name());
        assertEquals(Span.Kind.SERVER, spans.get(1).kind());
        List<EventMetric> metrics = mockMetricServer.waitForMetrics(2);
        assertNotNull(metrics);
        assertEquals(2, metrics.size());
    }

    @Test
    public void testHello() {
        dubboTestApi.hello("hi");
        List<Span> spans = mockTracerServer.waitForSpans(2, Duration.ofSeconds(5).toMillis());
        assertNotNull(spans);
        assertEquals(2, spans.size());
        assertEquals(spans.get(0).traceId(), spans.get(1).parentId());
        assertEquals("com.yametech.yangjian.agent.tests.dubbo.alibaba.DubboTestApi.hello(String)", spans.get(0).name());
        assertEquals(Span.Kind.CLIENT, spans.get(0).kind());
        assertEquals("com.yametech.yangjian.agent.tests.dubbo.alibaba.DubboTestApiImpl.hello(String)", spans.get(1).name());
        assertEquals(Span.Kind.SERVER, spans.get(1).kind());
        List<EventMetric> metrics = mockMetricServer.waitForMetrics(2);
        assertNotNull(metrics);
        assertEquals(2, metrics.size());
    }

    @Test
    public void testGenericService() {
        Object result = genericService.$invoke("genericCall", new String[]{"int", "java.lang.Long", "java.lang.String", "java.lang.Object[]"}, new Object[]{1, 2L, "GenericService", new Object[]{"a"}});
        assertEquals("GenericService", result);
        List<Span> spans = mockTracerServer.waitForSpans(2);
        assertNotNull(spans);
        assertEquals(2, spans.size());
        assertEquals(spans.get(0).traceId(), spans.get(1).parentId());
        assertEquals("com.yametech.yangjian.agent.tests.dubbo.alibaba.DubboTestApi.genericCall(int,Long,String,Object[])", spans.get(0).name());
        assertEquals(Span.Kind.CLIENT, spans.get(0).kind());
        assertEquals("com.yametech.yangjian.agent.tests.dubbo.alibaba.DubboTestApiImpl.genericCall(int,Long,String,Object[])", spans.get(1).name());
        assertEquals(Span.Kind.SERVER, spans.get(1).kind());
        List<EventMetric> metrics = mockMetricServer.waitForMetrics(2);
        assertNotNull(metrics);
        assertEquals(2, metrics.size());
        assertEquals("com.yametech.yangjian.agent.tests.dubbo.alibaba.DubboTestApiImpl.genericCall(int,Long,String,Object[])", metrics.get(0).getSign());
        assertEquals("com.yametech.yangjian.agent.tests.dubbo.alibaba.DubboTestApi.genericCall(int,Long,String,Object[])", metrics.get(1).getSign());
    }

    private static ReferenceConfig<DubboTestApi> referenceConfig;

    private static ServiceConfig<DubboTestApi> serviceConfig;

    private static ReferenceConfig<GenericService> genericReferenceConfig;

    private static void initDubboConfig() {
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

        genericReferenceConfig = new ReferenceConfig<>();
        genericReferenceConfig.setApplication(providerAppConfig);
        genericReferenceConfig.setInterface(DubboTestApi.class);
        genericReferenceConfig.setUrl("dubbo://localhost:" + getPort());
        genericReferenceConfig.setGeneric(true);
    }

    private static int getPort() {
        return 20901;
    }
}
