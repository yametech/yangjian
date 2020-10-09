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
package com.yametech.yangjian.agent.core.jvm.command;

import com.yametech.yangjian.agent.util.OSUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author zcn
 * @date: 2019-10-22
 */
public class ScriptRunnerTest {

    @Test
    public void testRunInWindows() {
        if(OSUtil.isWindows()) {
            CommandResult result = new CommandExecutor().execute(new String[]{"ipconfig"});
            Assert.assertTrue(result.isSuccess());
            Assert.assertTrue(result.getContent().length > 0);
        }else{
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testRunInLinux(){
        if(OSUtil.isLinux()){
            CommandResult result = new CommandExecutor().execute(new String[]{"top -b -n 1"});
            Assert.assertTrue(result.isSuccess());
            Assert.assertTrue(result.getContent().length > 0);
        }else{
            Assert.assertTrue(true);
        }
    }

}
