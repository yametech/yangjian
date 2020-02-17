package cn.ecpark.tool.agent.core.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadFactory implements ThreadFactory {
    private static final AtomicInteger THREAD_NUMBER = new AtomicInteger(1);
    private final boolean daemon;
    private final ThreadGroup group;
    private final int priority;
    private final String threadNamePrefix;

    public CustomThreadFactory(final String threadNamePrefix, final boolean daemon) {
        this(threadNamePrefix, daemon, 5);
    }

    public CustomThreadFactory(final String threadNamePrefix, final boolean daemon, final int priority) {
        this.threadNamePrefix = threadNamePrefix + "-";
        this.daemon = daemon;
        this.priority = priority;
        final SecurityManager securityManager = System.getSecurityManager();
        this.group = securityManager != null ? securityManager.getThreadGroup()
                : Thread.currentThread().getThreadGroup();
    }

    @Override
    public Thread newThread(final Runnable runnable) {
        final Thread thread = new Thread(group, runnable, threadNamePrefix + THREAD_NUMBER.getAndIncrement(), 0);
        if (thread.isDaemon() != daemon) {
            thread.setDaemon(daemon);
        }
        if (thread.getPriority() != priority) {
            thread.setPriority(priority);
        }
        thread.setUncaughtExceptionHandler(new ExceptionHandler());
        return thread;
    }

}
