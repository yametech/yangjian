package cn.ecpark.tool.agent.plugin.rabbitmq.bean;

public class MqInfo {
	private String ipPorts;// ip、端口，如：192.168.1.1:9092,192.168.1.1:9093
	private String topic;// 主题
	private String consumeGroup;// 消费标识，kafka为consumeGroup，rabbitmq为queue
//	private Map<String, Object> params;// 其他额外参数：如rabbitmq会有queue

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
	
	public MqInfo copy() {
		return new MqInfo(ipPorts, topic, consumeGroup);
	}
}
