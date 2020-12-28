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

import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.jvm.collector.CPUCollector;
import com.yametech.yangjian.agent.core.jvm.command.CommandExecutor;
import com.yametech.yangjian.agent.core.jvm.command.CommandResult;
import com.yametech.yangjian.agent.core.jvm.metrics.ProcessMetrics;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zcn
 * @date: 2019-10-21
 */
public enum LinuxProcessProvider {

    /**
     * singleton instance
     */
    INSTANCE;

    private static ILogger logger = LoggerFactory.getLogger(LinuxProcessProvider.class);

    private static Integer PID = getPid();

    private static CPUCollector cpuCollector = new CPUCollector();

    private static String MEMORY_COMMAND = MessageFormat.format("cat /proc/{0}/status | grep VmRSS", PID.toString());

    private static Pattern MEMORY_PATTERN = Pattern.compile("[^0-9]*(\\d*)[^0-9]*");

    private static final String FILE_MEM_INFO = "/proc/meminfo";

    private static long sysMemTotal = getSystemMemory().getOrDefault("MemTotal", 0L);

    private static Integer getPid() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        // format: "pid@hostname"
        String name = runtime.getName();
        return Integer.parseInt(name.substring(0, name.indexOf('@')));
    }

    public ProcessMetrics getProcessMetrics() {
        ProcessMetrics metrics = new ProcessMetrics();
        try {
            metrics.setMemoryUsage(getMemory());
            metrics.setCpuUsagePercent(cpuCollector.collect().getUsagePercent());
            metrics.setSysMemTotal(sysMemTotal);
        } catch (Exception e) {
            logger.error(e, "Fail to get process metrics");
        }
        return metrics;
    }

    private double getMemory() {
        CommandResult result = new CommandExecutor().execute(new String[]{"/bin/sh", "-c", MEMORY_COMMAND});
        if (!result.isSuccess()) {
            logger.error("Fail to get memory,{}", new String(result.getContent()));
            return 0d;
        }
        String data = new String(result.getContent(), StandardCharsets.UTF_8);
        Matcher matcher = MEMORY_PATTERN.matcher(data);
        if (matcher.matches()) {
            return Double.valueOf(matcher.group(1));
        }
        logger.error("Fail to match memory,{}", data);
        return 0d;
    }

    private static Map<String, Long> getSystemMemory() {
        Map<String, Long> memInfo = new HashMap<>();
        try {
            Path path = Paths.get(FILE_MEM_INFO);
            Files.lines(path).forEach((line) -> {
                if (line == null || "".equals(line)) {
                    return;
                }
                int beginIndex = 0;
                int endIndex = line.indexOf(":");
                if (endIndex != -1) {
                    String key = line.substring(beginIndex, endIndex);
                    beginIndex = endIndex + 1;
                    endIndex = line.length();
                    String memory = line.substring(beginIndex, endIndex);
                    memInfo.put(key, Long.valueOf(memory.replace("kB", "").trim()));
                }
            });
        } catch (Exception e) {
            logger.error(e, "Fail to getSystemMemory");
        }
        return memInfo;
    }
}
