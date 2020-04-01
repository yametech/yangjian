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
package com.yametech.yangjian.agent.server.service;

import com.yametech.yangjian.agent.protocol.ReportEntity;
import com.yametech.yangjian.agent.protocol.ReportRequest;
import com.yametech.yangjian.agent.protocol.ReportResponse;
import com.yametech.yangjian.agent.protocol.ReportServiceGrpc;
import com.yametech.yangjian.agent.server.config.ContextKey;
import com.yametech.yangjian.agent.server.storage.DiskMetricStore;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author dengliming
 * @date 2020/3/30
 */
@GrpcService
public class GrpcReportService extends ReportServiceGrpc.ReportServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcReportService.class);

    private final DiskMetricStore diskMetricStore;

    public GrpcReportService(DiskMetricStore diskMetricStore) {
        this.diskMetricStore = diskMetricStore;
    }

    @Override
    public void report(ReportRequest request, StreamObserver<ReportResponse> responseObserver) {
        try {
            String remoteAddress = ContextKey.REMOTE_ADDRESS.get();
            if (StringUtils.isNotBlank(remoteAddress)) {
                List<ReportEntity> reportEntities = request.getReportEntityList();
                for (ReportEntity reportEntity : reportEntities) {
                    Map<String, String> params = reportEntity.getParamsMap();
                    StringBuilder builder = new StringBuilder();
                    builder.append("serviceName=").append(reportEntity.getServiceName()).append('&');
                    builder.append("second=").append(reportEntity.getTimestamp()).append('&');
                    builder.append("ip=").append(remoteAddress);
                    for (Map.Entry<String, String> param : params.entrySet()) {
                        builder.append(param.getKey()).append('=').append(param.getValue()).append('&');
                    }
                    diskMetricStore.write(builder.toString());
                }
            }
            ReportResponse response = ReportResponse.newBuilder().setMsg("OK").build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.error("report error.", e);
        }
    }
}
