/**
 * <pre>
 * 基于disruptor封装的应用内部事件，简化了使用方式；
 * 	支持多线程消费、消费异常重试、缓存满后丢弃数据；
 * </pre>
 */
package com.yametech.yangjian.agent.util.eventbus.process;