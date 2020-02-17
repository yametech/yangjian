package cn.ecpark.tool.agent.plugin.kafka.bean;

import java.util.Map;

public class KafkaEventRT {
	private Map<String, Integer> topicNum;// 拉取的每个topic的条数
	private long startTime;// 消费开始时间
	private long useTime;// 从上次拉取结束到本次开始拉取的耗时毫秒数
	
	public KafkaEventRT(Map<String, Integer> topicNum, long startTime, long useTime) {
		this.topicNum = topicNum;
		this.startTime = startTime;
		this.useTime = useTime;
	}
	
	public Map<String, Integer> getTopicNum() {
		return topicNum;
	}
	
	public void setTopicNum(Map<String, Integer> topicNum) {
		this.topicNum = topicNum;
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	public long getUseTime() {
		return useTime;
	}
	
	public void setUseTime(long useTime) {
		this.useTime = useTime;
	}
	
}
