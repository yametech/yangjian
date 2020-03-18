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
package com.yametech.yangjian.agent.exporter.controller;

import com.yametech.yangjian.agent.exporter.storage.DiskMetricStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 供探针客户端通过Http方式上报
 *
 * @author dengliming
 * @date 2020/3/5
 */
@RestController
public class ReportController extends BaseController {

    private final DiskMetricStore diskMetricStore;

    public ReportController(DiskMetricStore diskMetricStore) {
        this.diskMetricStore = diskMetricStore;
    }

    @PostMapping(value = "/report")
    public String report(@RequestParam Map<String, Object> map, HttpServletRequest request) {
        String ip = getRequestIp(request);
        map.put("ip", ip);
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> param : map.entrySet()) {
            builder.append(param.getKey()).append('=').append(param.getValue()).append('&');
        }
        builder.deleteCharAt(builder.length() - 1);
        diskMetricStore.write(builder.toString());
        return "ok";
    }
}
