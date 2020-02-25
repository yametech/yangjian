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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.core.config.Config;
import com.yametech.yangjian.agent.core.log.ILogger;
import com.yametech.yangjian.agent.core.log.LoggerFactory;

public class LogUtil {
	private static ILogger log = LoggerFactory.getLogger(LogUtil.class);
	private static final char URL_SPLIT = '/';
	private static Map<String, Map<String, Object>> params = new HashMap<>();
	
	/**
	 * 打印统一的日志格式：应用名称/当前秒数/{path}?{key}={value}&{key}={value}...
	 * @param path	url后缀，不包含?及参数，不以/开头
	 * @param kvs	参数key/value，会做URLEncoder.encode
	 */
	@SafeVarargs
	public static void println(String path, Entry<String, Object>... kvs) {
		println(System.currentTimeMillis() / 1000, path, true, Arrays.asList(kvs));
	}
	@SafeVarargs
	public static void println(String path, boolean canRepeat, Entry<String, Object>... kvs) {
		println(System.currentTimeMillis() / 1000, path, canRepeat, Arrays.asList(kvs));
	}
	
	public static void println(String path, boolean canRepeat, List<Entry<String, Object>> kvs) {
		println(System.currentTimeMillis() / 1000, path, canRepeat, kvs);
	}
	
	@SafeVarargs
	public static void println(long second, String path, boolean canRepeat, Entry<String, Object>... kvs) {
		println(second, path, canRepeat, Arrays.asList(kvs));
	}
	
	/**
	 * 
	 * @param second	当前日志的数据时间
	 * @param path		当前数据的标识
	 * @param canRepeat	当前日志是否可以重复打印，如果当前打印的所有参数key-value与前一次的参数一样，即为重复，此时跳过日志输出，如果其中一个参数不一样，则输出
	 * @param kvs
	 */
	private static void println(long second, String path, boolean canRepeat, List<Entry<String, Object>> kvs) {
		if(path == null || path.contains("?")) {
			throw new RuntimeException("参数错误");
		}
		StringBuilder builder = new StringBuilder();
		builder.append(Config.SERVICE_NAME.getValue()).append(URL_SPLIT)
				.append(second).append(URL_SPLIT).append(path);
		if(kvs == null || kvs.isEmpty()) {
			log.info(builder.toString());
			return;
		}
		if(!canRepeat && isRepeat(path, kvs)) {
			return;
		}
		
		builder.append('?');
		for(Entry<String, Object> kv : kvs) {
			builder.append(encode(kv.getKey())).append('=').append(encode(kv.getValue())).append('&');
		}
		builder.deleteCharAt(builder.length() - 1);
		log.info(builder.toString());
		setParams(path, kvs);
	}
	
	private static void setParams(String path, List<Entry<String, Object>> kvs) {
		Map<String, Object> pathParams = params.get(path);
		if(pathParams == null) {
			pathParams = new HashMap<>();
			params.put(path, pathParams);
		}
		for(Entry<String, Object> kv : kvs) {
			pathParams.put(kv.getKey(), kv.getValue());
		}
	}
	
	private static boolean isRepeat(String path, List<Entry<String, Object>> kvs) {
		Map<String, Object> pathParams = params.get(path);
		if(pathParams == null || pathParams.size() == 0) {
			return false;
		}
		for(Entry<String, Object> kv : kvs) {
			if(kv.getValue() == null || kv.getKey() == null) {
				continue;
			}
			if(!kv.getValue().equals(pathParams.get(kv.getKey()))) {
				return false;
			}
		}
		return true;
	}
	
	private static String encode(Object value) {
		if(value == null) {
			return "";
		}
		return StringUtil.encode(value.toString());
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		String encode = StringUtil.encode("test=123&test--中文");
		System.err.println(encode);
		System.err.println(URLDecoder.decode(encode, "UTF-8"));
	}
}
