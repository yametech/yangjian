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

package com.yametech.yangjian.agent.core.util;

import com.yametech.yangjian.agent.api.ISchedule;
import com.yametech.yangjian.agent.core.log.ILogger;
import com.yametech.yangjian.agent.core.log.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 为了测试后面去掉
 *
 * @author dengliming
 * @date 2020/1/2
 */
public class ThreadWatcher implements ISchedule {
    private static final ILogger logger = LoggerFactory.getLogger(ThreadWatcher.class);
    private final static List<Thread> threadList = new ArrayList<>();

    public static void addThread(Thread thread) {
        threadList.add(thread);
    }

    @Override
    public int interval() {
        return 2;
    }

    @Override
    public void execute() {
        for (Thread thread : threadList) {
            logger.info("Thread[id:{},name:{},state:{}] print.", thread.getId(), thread.getName(), thread.getState(), thread.toString());
        }
    }
}
