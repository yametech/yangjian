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
package com.yametech.yangjian.agent.plugin.druid;

import com.yametech.yangjian.agent.api.IMetricMatcher;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.configmatch.*;
import com.yametech.yangjian.agent.api.configmatch.*;

import java.util.Arrays;

/**
 * @author dengliming
 * @date 2019/12/22
 */
public class DruidConnectionMatcher implements IMetricMatcher {

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new ClassMatch("com.alibaba.druid.pool.DruidDataSource"),
                new MethodNameMatch("getConnection"),
                new MethodArgumentNumMatch(1),
                new MethodArgumentIndexMatch(0, "long")
        ));
    }

    @Override
    public String type() {
        return Constants.EventType.DRUID;
    }

    @Override
    public LoadClassKey loadClass(MethodType type) {
        return new LoadClassKey("com.yametech.yangjian.agent.plugin.druid.DruidConnectionConvert");
    }
}
