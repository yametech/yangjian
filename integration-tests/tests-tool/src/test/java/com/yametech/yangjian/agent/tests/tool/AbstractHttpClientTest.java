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

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Before;
import org.junit.Rule;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.seeOther;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

/**
 * @author dengliming
 * @date 2020/9/22
 */
public abstract class AbstractHttpClientTest extends AbstractAgentTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.wireMockConfig().dynamicPort(), false);

    @Before
    public final void setUpWiremock() {
        wireMockRule.stubFor(any(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(200)));
        wireMockRule.stubFor(get(urlEqualTo("/error"))
                .willReturn(aResponse()
                        .withStatus(500)));
        wireMockRule.stubFor(get(urlEqualTo("/redirect"))
                .willReturn(seeOther("/")));
    }

    protected String getBaseUrl() {
        return "http://localhost:" + wireMockRule.port();
    }
}
