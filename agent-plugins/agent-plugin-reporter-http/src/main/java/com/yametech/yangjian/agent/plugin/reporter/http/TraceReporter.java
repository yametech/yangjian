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
package com.yametech.yangjian.agent.plugin.reporter.http;

/**
 * HTTP上报
 * <p>
 * 注：agent.properties配置report.spanListener=http-span、report.http-span.url=xxx
 *
 * @author dengliming
 * @date 2020/3/5
 */
public class TraceReporter extends AbstractHttpReporter {

    @Override
    public String type() {
        return "http-span";
    }

    @Override
    public String getConfigKey() {
        return "report.http-span.url";
    }

}
