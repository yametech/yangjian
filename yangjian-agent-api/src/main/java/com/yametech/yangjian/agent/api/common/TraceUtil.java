package com.yametech.yangjian.agent.api.common;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

public class TraceUtil {

	/**
	 * 获取当前时间的微秒数
	 * 
	 * @return 微妙数
	 */
	public static long nowMicros() {
		Instant now = Instant.now();// 不能缓存，如果换成每次获取的时间都是一样
		return (now.getEpochSecond() * 1000000) + (now.getNano() / 1000);
	}

	/**
	 * 获取当前时间的毫秒数
	 * 
	 * @return 毫秒数
	 */
	public static long nowMillis() {
		return Instant.now().toEpochMilli();
	}

	/**
	 * 获取随机long类型ID，目前可用于生成spanId、traceId
	 * 
	 * @return 随机ID
	 */
	public static long generateRandomId() {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		long id;
		do {
			id = random.nextLong();
		} while (id == 0);
		return id;
	}

}
