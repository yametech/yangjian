package com.yametech.yangjian.agent.api.common;

import java.net.URI;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

public class TraceUtil {
	private static long previousMicros = -1L;
	/**
	 * 获取当前时间的微秒数，微秒部分基本都是000，精确度有问题，基本等价于System.currentTimeMillis() * 1000
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

	/**
	 * 获取端口号
	 *
	 * @param url
	 * @return
	 */
	public static int getPort(URI url) {
		int port = url.getPort();
		return port > 0 ? port : "https".equals(url.getScheme().toLowerCase()) ? 443 : 80;
	}
}
