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
package com.yametech.yangjian.agent.api.log;

/**
 * 默认日志实现类（不作任何操作）
 *
 * @author dengliming
 */
public class NOPLogger implements ILogger {

    public static final NOPLogger NOP_LOGGER = new NOPLogger();

    @Override
    public boolean isDebugEnable() {
        return false;
    }

    @Override
    public boolean isInfoEnable() {
        return false;
    }

    @Override
    public boolean isWarnEnable() {
        return false;
    }

    @Override
    public boolean isErrorEnable() {
        return false;
    }

    @Override
    public void debug(String format) {

    }

    @Override
    public void debug(String format, Object... arguments) {

    }

    @Override
    public void info(String format) {

    }

    @Override
    public void info(String format, Object... arguments) {

    }

    @Override
    public void warn(String format) {

    }

    @Override
    public void warn(String format, Object... arguments) {

    }

    @Override
    public void warn(Throwable e, String format, Object... arguments) {

    }

    @Override
    public void error(String format) {

    }

    @Override
    public void error(String format, Object... arguments) {

    }

    @Override
    public void error(Throwable e, String format, Object... arguments) {

    }
}
