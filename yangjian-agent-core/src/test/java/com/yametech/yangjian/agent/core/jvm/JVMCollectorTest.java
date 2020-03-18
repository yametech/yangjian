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
package com.yametech.yangjian.agent.core.jvm;

import org.junit.Test;

/**
 * @author zcn
 * @date: 2019-10-17
 **/
public class JVMCollectorTest {

    @Test
    public void test(){
        long totalMemory = 16432216;

        double cpu = 1.9d;
        double mem = 12.8d;

        System.out.println(cpu / 100);
        System.out.println( totalMemory * (mem / 100));
    }
}
