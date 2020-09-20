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
package com.yametech.yangjian.agent.api.base;

import java.util.List;

public interface IReportData {
	
	/**
	 * 单条上报
	 * @param data	一条上报的数据
	 * @return	是否上报成功
	 */
	boolean report(Object data);
	
	/**
	 * 
	 * 批量上报数据
	 * @param datas	需要上报的数据
	 * @return	是否上报成功
	 */
	boolean batchReport(List<Object> datas);
}
