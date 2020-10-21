package com.yametech.yangjian.agent.core.trace;

import java.util.Map;

import zipkin2.Span.Kind;

public class TraceSpan {
	private String traceId;// 链路ID
	private Long id;// 当前span id
	private Long parentId;// 当前span的父span ID
	private Component component;// 类别，如：kafka、dubbo、rabbitmq、mysql、jedis
	private String name;// span名称，operateName，有长度限制200，如：VehicleInfoService.findVehicleInfoByPlateNo(String)、Topic/Producer、findcar/get
	private Kind kind;// 类型，server、client、consumer、producer
	private boolean error = false;// 状态是否正常
	private long timestamp;// 请求开始时间（微秒）
	private long duration;// 请求耗时（微秒）
//	private long startTime;// 开始事件毫秒数
//	private long endTime;// 结束事件毫秒数
	private Map<String, String> localEndpoint;// 本地网络信息
	private Map<String, String> remoteEndpoint;// 远程网络信息
	
	private Map<String, String> tags;// 标签，与日志的区别在于tags一般用于搜索（存储上会增加索引），可放入一些常用排查问题的业务字段
	private Map<Long, Map<String, String>> logs;// 日志信息，仅用于展示，不可搜索
	
	
}
