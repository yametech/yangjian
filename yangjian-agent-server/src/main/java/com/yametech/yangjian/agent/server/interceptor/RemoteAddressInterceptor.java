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
package com.yametech.yangjian.agent.server.interceptor;

import static com.yametech.yangjian.agent.server.config.ContextKey.REMOTE_ADDRESS;

import io.grpc.ServerInterceptor;
import io.grpc.ServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCallHandler;
import io.grpc.Context;
import io.grpc.Grpc;
import io.grpc.Contexts;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;

/**
 * 拦截器（目的为了获取GRPC客户端的IP地址）
 *
 * @author dengliming
 * @date 2020/3/31
 */
@Component
@GrpcGlobalServerInterceptor
public class RemoteAddressInterceptor implements ServerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteAddressInterceptor.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
                                                                 ServerCallHandler<ReqT, RespT> next) {
        Context context = Context.current();
        try {
            SocketAddress socketAddress = call.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR);
            if (socketAddress != null) {
                String host = socketAddress.toString().split(":")[0];
                if (host.startsWith("/")) {
                    host = host.substring(1);
                }

                context = context.withValue(REMOTE_ADDRESS, host);
            }
        } catch (Exception e) {
            LOGGER.error("Intercept RemoteAddress error.", e);
        }
        return Contexts.interceptCall(context, call, headers, next);
    }
}
