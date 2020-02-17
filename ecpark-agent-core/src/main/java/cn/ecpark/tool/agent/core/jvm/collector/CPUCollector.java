package cn.ecpark.tool.agent.core.jvm.collector;

import cn.ecpark.tool.agent.core.jvm.metrics.CPUMetrics;
import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;


/**
 * @author zcn
 * @date: 2019-10-17
 **/
public final class CPUCollector implements IMetricsCollector {

    private OperatingSystemMXBean operatingSystemMXBean;
    private int processor;
    private long lastCpuTime;
    private long lastTime;

    public CPUCollector() {
        this.operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        this.processor = operatingSystemMXBean.getAvailableProcessors();

        this.lastCpuTime = operatingSystemMXBean.getProcessCpuTime();
        this.lastTime = System.nanoTime();
    }

    @Override
    public CPUMetrics collect() {
        return new CPUMetrics(calculateCpuUsage(), processor);
    }

    private double calculateCpuUsage() {
        long cpuTime = operatingSystemMXBean.getProcessCpuTime();
        long now = System.nanoTime();
        long cpuCost = cpuTime - lastCpuTime;
        try {
            return cpuCost * 1.0d / ((now - lastTime) * processor) * 100;
        } finally {
            this.lastCpuTime = cpuTime;
            this.lastTime = now;
        }
    }

    //just for test
    public static void main(String[] args) throws Exception {
        CPUCollector cpuCollector = new CPUCollector();
        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                while (true) {
                    long bac = 1000000;
                    bac = bac >> 1;
                }
            }).start();
        }
        while (true) {
            Thread.sleep(2000);
            System.out.println(cpuCollector.calculateCpuUsage());
        }
    }
}

