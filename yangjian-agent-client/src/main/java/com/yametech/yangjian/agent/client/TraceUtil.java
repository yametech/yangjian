package com.yametech.yangjian.agent.client;

import java.util.Map;
import java.util.function.Supplier;

public class TraceUtil {
	private TraceUtil() {}
	
	/**
	 * 标记执行supplier逻辑的链路信息，使用该方法包装的逻辑如果接入了yangjian-agent会自动生成链路节点
	 * @param supplier	需要执行的逻辑
	 * @return	自定义返回值
	 */
	public static <T> T mark(Supplier<T> supplier) {
		return mark(null, supplier);
	}
	
	/**
	 * 	标记执行supplier逻辑的链路信息，使用该方法包装的逻辑如果接入了yangjian-agent会自动生成链路节点
	 * @param tags	链路标签
	 * @param supplier	需要执行的逻辑
	 * @return	自定义返回值
	 */
	public static <T> T mark(Map<String, String> tags, Supplier<T> supplier) {
		return mark(true, tags, supplier);
	}
	
	/**
	 * 	标记执行supplier逻辑的链路信息，使用该方法包装的逻辑如果接入了yangjian-agent会自动生成链路节点
	 * @param sample	是否生成链路信息，如果为false则不记录链路信息，主要用于根据不同情况决定是否产生链路节点；如果为true，则使用链路的自定义采样率生成链路节点（不一定全采样）
	 * @param tags	链路标签
	 * @param supplier	需要执行的逻辑
	 * @return	自定义返回值
	 */
	public static <T> T mark(boolean sample, Map<String, String> tags, Supplier<T> supplier) {
		return supplier.get();
	}
	
	public static void main(String[] args) {
		System.err.println(mark(true, null, () -> "test"));
	}
}
