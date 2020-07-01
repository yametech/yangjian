package com.yametech.yangjian.agent.plugin.kafka.common;

import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.bean.MetricData;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.LRUCache;
import com.yametech.yangjian.agent.api.common.MultiReportFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dengliming
 * @date 2020/6/24
 */
public class KafkaUtil {

    private static IReportData report = MultiReportFactory.getReport("collect");
    private static final LRUCache CONNECT_URL_CACHE = new LRUCache(5);

    public static void reportDependency(String url) {
        if (!CONNECT_URL_CACHE.containsKey(url)) {
            Map<String, Object> params = new HashMap<>();
            params.put(Constants.Tags.PEER, url);
            report.report(MetricData.get(null, Constants.DEPENDENCY_PATH + Constants.Component.KAFKA, params));
            CONNECT_URL_CACHE.put(url, true);
        }
    }
}
