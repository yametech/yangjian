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
package com.yametech.yangjian.agent.plugin.spring.webflux.context;


/**
 * @author dengliming
 * @date 2020/3/18
 */
public class ContextConstants {

    public static String REQUEST_EVENT_CONTEXT_KEY = "__req_event___";
    public static String RESPONSE_STATUS_CONTEXT_KEY = "__res_status__";
    public static String REQUEST_HEADER_CONTEXT_KEY = "__req_header__";


    public static final String SERVER_SPAN_CONTEXT = "TracingWebFilter.activeSpanContext";

}
