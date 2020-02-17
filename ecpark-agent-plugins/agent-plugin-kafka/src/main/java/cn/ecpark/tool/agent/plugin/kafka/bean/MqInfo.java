package cn.ecpark.tool.agent.plugin.kafka.bean;

public class MqInfo {
	private String ipPorts;// ip、端口，如：192.168.1.1:9092,192.168.1.1:9093
	private String topic;// 主题
	private String consumeGroup;// 消费标识，kafka为consumeGroup，rabbitmq为queue

	public MqInfo(String ipPorts, String topic, String consumeGroup) {
		this.ipPorts = ipPorts;
		this.topic = topic;
		this.consumeGroup = consumeGroup;
	}

	public String getIpPorts() {
		return ipPorts;
	}

	public String getTopic() {
		return topic;
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getConsumeGroup() {
		return consumeGroup;
	}
	
	public void setConsumeGroup(String consumeGroup) {
		this.consumeGroup = consumeGroup;
	}
	
}
