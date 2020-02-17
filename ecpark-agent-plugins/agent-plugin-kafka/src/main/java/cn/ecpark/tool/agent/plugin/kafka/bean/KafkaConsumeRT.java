package cn.ecpark.tool.agent.plugin.kafka.bean;

import java.util.Map;

public class KafkaConsumeRT {
	private Map<String, Integer> topicNum;// 拉取的每个topic的条数
	private long startTime;// 数据拉取结束时间毫秒数
	
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
	
}
