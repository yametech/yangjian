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
package com.yametech.yangjian.agent.plugin.reporter.grpc;

import com.yametech.yangjian.agent.api.IAppStatusListener;
import com.yametech.yangjian.agent.api.IConfigReader;
import com.yametech.yangjian.agent.api.IReport;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.protocol.ReportEntity;
import com.yametech.yangjian.agent.protocol.ReportRequest;
import com.yametech.yangjian.agent.protocol.ReportServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * GRPC上报
 *
 * 注：agent.properties配置report=grpc
 *
 * @author dengliming
 * @date 2020/3/30
 */
public class GrpcReporter implements IReport, IAppStatusListener, IConfigReader {

    private static final ILogger LOGGER = LoggerFactory.getLogger(GrpcReporter.class);
    private ReportServiceGrpc.ReportServiceBlockingStub serviceStub;
    private ManagedChannel channel;
    private static final String REPORT_GRPC_URL_KEY = "report.grpc.url";
    private static final String REPORT_GZIP_ENABLE_KEY = "report.grpc.gzip.enable";
    private static final String SERVICE_NAME_KEY = "service.name";
    private String server;
    private boolean gzipEnabled = false;
    private String serviceName;
    private static final int GRPC_DEFAULT_TIMEOUT = 1;

    @Override
    public void beforeRun() {
        if (StringUtil.isEmpty(server)) {
            LOGGER.error("ReportServiceClient init fail(server is empty)");
            return;
        }
        List<String> servers = Arrays.stream(server.split(",")).collect(Collectors.toList());
        /*channel = ManagedChannelBuilder.forAddress(ipAndPort[0], Integer.parseInt(ipAndPort[1]))
                .usePlaintext()
                .build();*/
        channel = ManagedChannelBuilder
                // 设置连接的目标地址
                .forTarget("local")
                // 设置地址服务
                .nameResolverFactory(new LocalNameResolverProvider(servers))
                // 设置轮询策略
                .defaultLoadBalancingPolicy("round_robin")
                .usePlaintext()
                .build();
        serviceStub = ReportServiceGrpc.newBlockingStub(channel);
        if (gzipEnabled) {
            serviceStub = serviceStub.withCompression("gzip");
        }
    }

    @Override
    public boolean shutdown(Duration duration) {
        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error(e, "shutdown error.");
        }
        return true;
    }

    @Override
    public Set<String> configKey() {
        return new HashSet<>(Arrays.asList(REPORT_GRPC_URL_KEY, REPORT_GZIP_ENABLE_KEY, SERVICE_NAME_KEY));
    }

    @Override
    public void configKeyValue(Map<String, String> kv) {
        server = kv.get(REPORT_GRPC_URL_KEY);
        gzipEnabled = "true".equals(kv.get(REPORT_GZIP_ENABLE_KEY));
        serviceName = kv.get(SERVICE_NAME_KEY);
    }

    @Override
    public String type() {
        return "grpc";
    }

    @Override
    public boolean report(String dataType, Long second, Map<String, Object> params) {
        if (serviceStub == null || channel == null || params == null || params.isEmpty()) {
            return false;
        }

        if (second == null) {
            second = System.currentTimeMillis() / 1000;
        }
        try {
            params.put("dataType", dataType);
            ReportRequest request = ReportRequest.newBuilder()
                    .addReportEntity(ReportEntity.newBuilder()
                            .setServiceName(serviceName)
                            .setTimestamp(second)
                            .putAllParams(params.entrySet().stream()
                                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString())))
                            .build())
                    .build();
            serviceStub.withDeadlineAfter(GRPC_DEFAULT_TIMEOUT, TimeUnit.SECONDS).report(request);
        } catch (StatusRuntimeException e) {
            LOGGER.error("RPC report failed: {}", e.getStatus());
        }
        return true;
    }
}
