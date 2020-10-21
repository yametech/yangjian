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
package com.yametech.yangjian.agent.core.report;

import java.util.List;

import com.yametech.yangjian.agent.api.IReport;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;

public class LogReport implements IReport {
	private static ILogger log = LoggerFactory.getLogger(LogReport.class);
	
	@Override
	public boolean report(Object data) {
		log.info(data.toString());
		return true;
	}
	
	@Override
	public boolean batchReport(List<Object> datas) {
		for(Object obj : datas) {
			if(!report(obj)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String type() {
		return Constants.ReportType.LOG;
	}

}
