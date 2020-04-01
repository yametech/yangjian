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

import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dengliming
 * @date 2020/3/31
 */
public class LocalNameResolver extends NameResolver {

    /**
     * rpc地址的配置列表 host1:port1,host2:port2
     */
    private List<String> servers;

    public LocalNameResolver(List<String> servers) {
        this.servers = servers;
    }

    @Override
    public String getServiceAuthority() {
        return "none";
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void start(Listener listener) {
        List<EquivalentAddressGroup> addressGroups = new ArrayList<>();
        for (String server : servers) {
            if (server.trim().length() > 0) {
                String[] ipAndPort = server.split(":");
                List<SocketAddress> socketAddresses = new ArrayList<>();
                socketAddresses.add(new InetSocketAddress(ipAndPort[0], Integer.parseInt(ipAndPort[1])));
                addressGroups.add(new EquivalentAddressGroup(socketAddresses));
            }
        }
        listener.onAddresses(addressGroups, Attributes.EMPTY);
    }
}
