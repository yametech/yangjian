package com.yametech.yangjian.agent.plugin.lettuce.util;

import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.bean.MetricData;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.MultiReportFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dengliming
 * @date 2020/6/22
 */
public class RedisUtil {

    private static IReportData report = MultiReportFactory.getReport("collect");

    public static void reportDependency(String url) {
        Map<String, Object> params = new HashMap<>();
        params.put(Constants.Tags.PEER, url);
        report.report(MetricData.get(null, Constants.DEPENDENCY_PATH + Constants.Component.LETTUCE, params));
    }
}
