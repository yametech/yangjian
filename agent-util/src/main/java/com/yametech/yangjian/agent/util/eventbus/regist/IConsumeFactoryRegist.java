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
package com.yametech.yangjian.agent.util.eventbus.regist;


import java.util.List;

import com.yametech.yangjian.agent.util.eventbus.consume.ConsumeFactory;

/**
 * 通过注册消费工厂实例，可以保证多线程消费时，每个实例仅被一个线程调用
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年10月11日 下午4:50:12
 */
public interface IConsumeFactoryRegist extends IRegist {

    /**
     * 返回需加载的消费者工厂实例
     * @return
     */
	@Override
	List<ConsumeFactory<?>> regist();
}
