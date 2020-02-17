package cn.ecpark.tool.agent.core.jvm.process;

import cn.ecpark.tool.agent.core.jvm.collector.CPUCollector;
import cn.ecpark.tool.agent.core.jvm.command.CommandExecutor;
import cn.ecpark.tool.agent.core.jvm.command.CommandResult;
import cn.ecpark.tool.agent.core.jvm.metrics.ProcessMetrics;
import cn.ecpark.tool.agent.core.log.ILogger;
import cn.ecpark.tool.agent.core.log.LoggerFactory;

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
