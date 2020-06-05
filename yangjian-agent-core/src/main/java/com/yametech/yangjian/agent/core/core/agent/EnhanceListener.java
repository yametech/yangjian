package com.yametech.yangjian.agent.core.core.agent;

import com.yametech.yangjian.agent.api.base.IMatch;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public class EnhanceListener {
    private static final int MAX_SIZE = 500;
    private static final ILogger LOG = LoggerFactory.getLogger(EnhanceListener.class);
    private static final Map<String, MatchInfo> CLASS_MATCHES = new ConcurrentHashMap<>();
    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    private EnhanceListener() {}

    public static void register(String typeName, Set<IMatch> matches) {
        CLASS_MATCHES.put(typeName, new MatchInfo(matches));
        if(CLASS_MATCHES.size() <= MAX_SIZE) {
            return;
        }
        synchronized(CLASS_MATCHES) {
            if(CLASS_MATCHES.size() <= MAX_SIZE) {
                return;
            }
            CLASS_MATCHES.entrySet().stream()
                .min(Comparator.comparing(classMatch -> classMatch.getValue().getTime()))
                .ifPresent(classMatch -> {
                    CLASS_MATCHES.remove(classMatch.getKey());
                    LOG.warn("register too many enhance listener: {}，delete long time without notice listener {}", MAX_SIZE, classMatch);
                });
        }
    }

    public static void notifyAndUnregister(String typeName, ClassLoader classLoader, boolean loaded, Throwable throwable) {
        MatchInfo matchInfo = CLASS_MATCHES.remove(typeName);
        if(matchInfo == null) {
            return;
        }
        executor.execute(() -> matchInfo.getMatches().forEach(match -> {
            try {
                if(throwable == null) {
                    match.onComplete(typeName, classLoader, loaded);
                } else {
                    match.onError(typeName, classLoader, loaded, throwable);
                }
            } catch (Throwable e) {
                LOG.warn(e, "notify enhance exception:{}   {}	{}", match.match(), typeName, classLoader);
            }
        }));
    }

    static class MatchInfo {
        private Set<IMatch> matches;
        private long time = System.currentTimeMillis();

        public MatchInfo(Set<IMatch> matches) {
            this.matches = matches;
        }

        public Set<IMatch> getMatches() {
            return matches;
        }

        public long getTime() {
            return time;
        }

        @Override
        public String toString() {
            return "MatchInfo{" +
                    "time=" + time +
                    "，matches=" + StringUtil.join(matches.stream().map(IMatch::match).toArray()) +
                    '}';
        }
    }

}
