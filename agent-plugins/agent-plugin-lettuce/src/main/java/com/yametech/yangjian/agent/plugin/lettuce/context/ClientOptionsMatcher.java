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
package com.yametech.yangjian.agent.plugin.lettuce.context;

import com.yametech.yangjian.agent.api.IEnhanceClassMatch;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.SPI;
import com.yametech.yangjian.agent.api.configmatch.ClassMatch;

/**
 * @author dengliming
 * @date 2020/6/14
 */
public class ClientOptionsMatcher implements IEnhanceClassMatch, SPI {
    @Override
    public IConfigMatch classMatch() {
        return new ClassMatch("io.lettuce.core.ClientOptions");
    }
}
