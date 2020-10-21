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
package com.yametech.yangjian.agent.core.config;

import java.util.concurrent.TimeUnit;

import com.yametech.yangjian.agent.core.config.RemoteConfigLoader;

/**
 * @author dengliming
 * @date 2019/12/2
 */
public class RemoteConfigTest {

    public static void main(String[] args) {
        t();
    }

    public static void t() {
        RemoteConfigLoader remoteConfigReader = new RemoteConfigLoader();
        remoteConfigReader.load(null);
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
