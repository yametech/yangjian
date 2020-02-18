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

package com.yametech.yangjian.agent.util.eventbus.reactor.retrystrategy;

public class SimpleRetry implements IRetry {
    private int retryTimes = 3;// 重试次数，不含第一次调用
    private int intervalMillis = 100;// 重试间隔毫秒
    private int intervalFactor = 2;// 重试间隔因子

    public SimpleRetry retryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
        return this;
    }

    public SimpleRetry intervalMillis(int intervalMillis) {
        this.intervalMillis = intervalMillis;
        return this;
    }

    public SimpleRetry intervalFactor(int intervalFactor) {
        this.intervalFactor = intervalFactor;
        return this;
    }

    @Override
    public void call(Runnable run) {
        int thisRetryTimes = 0;
        Throwable throwable = null;
        do{
            try {
            	run.run();
                return;
            } catch(Throwable t) {
                throwable = t;
            }
            thisRetryTimes++;
            try {
                Thread.sleep(intervalMillis * intervalFactor * thisRetryTimes);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while(thisRetryTimes <= retryTimes);
        throw new RuntimeException("重试" + (thisRetryTimes - 1) + "次，依然失败", throwable);
    }
}
