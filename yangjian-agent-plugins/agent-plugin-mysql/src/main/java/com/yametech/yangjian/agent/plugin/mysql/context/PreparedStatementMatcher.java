/**
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

package com.yametech.yangjian.agent.plugin.mysql.context;

import java.util.Arrays;

import com.yametech.yangjian.agent.api.IEnhanceClassMatch;
import com.yametech.yangjian.agent.api.InterceptorMatcher;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.configmatch.ClassMatch;
import com.yametech.yangjian.agent.api.configmatch.CombineAndMatch;
import com.yametech.yangjian.agent.api.configmatch.CombineOrMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodArgumentIndexMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodRegexMatch;

/**
 * 增强类为了获取jdbcPreparedStatement执行的sql
 *
 * @author dengliming
 * @date 2019/11/27
 */
public class PreparedStatementMatcher implements InterceptorMatcher, IEnhanceClassMatch {

    @Override
    public IConfigMatch classMatch() {
        return new CombineOrMatch(Arrays.asList(
                // 5.x
                new ClassMatch("com.mysql.jdbc.PreparedStatement"),
                // 8.x
                new ClassMatch("com.mysql.cj.jdbc.ClientPreparedStatement"),
                // 6.x
                new ClassMatch("com.mysql.cj.jdbc.PreparedStatement"),
                // useServerPrepStmts=true时用到
                new ClassMatch("com.mysql.jdbc.ServerPreparedStatement"),
                new ClassMatch("com.mysql.cj.jdbc.ServerPreparedStatement")));
    }

    @Override
    public IConfigMatch match() {
        return new CombineAndMatch(Arrays.asList(
                new MethodRegexMatch(".*com\\.mysql(\\.cj)?\\.jdbc\\..*PreparedStatement\\(.*"),
                new MethodArgumentIndexMatch(1, "java.lang.String"))
        );
    }

    @Override
    public LoadClassKey loadClass(MethodType type) {
    	return new LoadClassKey("com.yametech.yangjian.agent.plugin.mysql.context.PreparedStatementInterceptor");
    }
}
