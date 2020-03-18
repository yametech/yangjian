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
package com.yametech.yangjian.agent.core.jvm.process;

import com.yametech.yangjian.agent.core.jvm.collector.CPUCollector;
import com.yametech.yangjian.agent.core.jvm.command.CommandExecutor;
import com.yametech.yangjian.agent.core.jvm.command.CommandResult;
import com.yametech.yangjian.agent.core.jvm.metrics.ProcessMetrics;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zcn
 * @date: 2019-10-21
 **/
public enum LinuxProcessProvider {

    INSTANCE;

    private static ILogger logger = LoggerFactory.getLogger(LinuxProcessProvider.class);

    private static Integer PID = getPid();

    private static CPUCollector cpuCollector = new CPUCollector();

    private static String MEMORY_COMMAND = MessageFormat.format("cat /proc/{0}/status | grep VmRSS", PID.toString());

    private static Pattern MEMORY_PATTERN = Pattern.compile("[^0-9]*(\\d*)[^0-9]*");

    private static Integer getPid() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName();  // format: "pid@hostname"
        return Integer.parseInt(name.substring(0, name.indexOf('@')));
    }

    public ProcessMetrics getProcessMetrics() {
        try {
            ProcessMetrics metrics = new ProcessMetrics();
            metrics.setMemoryUsage(getMemory());
            metrics.setCpuUsagePercent(cpuCollector.collect().getUsagePercent());

            return metrics;
        } catch (Exception e) {
            logger.error("Fail to get process metrics", e);
            return new ProcessMetrics();
        }
    }

    private double getMemory() throws Exception {
        CommandResult result = new CommandExecutor().execute(new String[]{"/bin/sh", "-c", MEMORY_COMMAND});
        if (result.isSuccess()) {
            String data = new String(result.getContent(), StandardCharsets.UTF_8);

            Matcher matcher = MEMORY_PATTERN.matcher(data);

            if (matcher.matches()) {
                return Double.valueOf(matcher.group(1));
            } else {
                logger.error("Fail to match memory," + data);
            }
            return 0d;
        }
        logger.error("Fail to get memory," + new String(result.getContent()));
        return 0d;
    }
}
