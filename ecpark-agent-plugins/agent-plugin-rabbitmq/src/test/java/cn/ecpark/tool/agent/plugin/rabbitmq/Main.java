package cn.ecpark.tool.agent.plugin.rabbitmq;

import com.alibaba.fastjson.JSON;

import cn.ecpark.plugin.mq.common.BaseConfig;
import cn.ecpark.plugin.mq.consumer.Consumer;
import cn.ecpark.plugin.mq.consumer.config.RabbitmqConsumeConfig;
import cn.ecpark.plugin.mq.producer.Publisher;
import cn.ecpark.plugin.mq.producer.config.RabbitmqPublishConfig;
import cn.ecpark.plugin.mq.serialization.StringSerializer;
import cn.ecpark.plugin.mq.util.Commons;

public class Main {
	private static final String TOPIC = "TOPIC_ECPARK_AGENT";
	private static final String CONSUME_GROUP = "GROUP_TOPIC_ECPARK_AGENT";
	private static final int TOPIC_NUM = 1;
	private static final int PUBLISH_NUM = 5;
	
	public static void main(String[] args) throws InterruptedException {
		publish();
		consume();
//		Thread.sleep(10000);
	}
	
	/**
	 * Map中存放的不是<String, Object>也没问题，无编译错误，但是消费时强转为<String, Boolean>会有问题
	 * @throws InterruptedException
	 */
	public static void publish() throws InterruptedException {
		RabbitmqPublishConfig config = new RabbitmqPublishConfig();
		setCommonConfig(config);
		config.setTopic(TOPIC);
		config.setShardTopicNum(TOPIC_NUM);
//		config.setMsgPersistent(false);
		Publisher publisher = new Publisher(config);
		for(int i = 0; i < PUBLISH_NUM; i++) {
			try {
				publisher.publish(i+"", "test-" + i);
//				Map<String, Boolean> data = new HashMap<>();
//				data.put("eee", true);
//				publisher.publish(i+"", data);
				System.err.println(">>>>>>>发布");
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		publisher.shutdown();
	}

	public static void consume() throws InterruptedException {
		RabbitmqConsumeConfig<String> config = new RabbitmqConsumeConfig<>();
		setCommonConfig(config);
		config.setTopics(Commons.getShardTopics(TOPIC, TOPIC_NUM));
		config.setQueueNames(Commons.getShardQueues(CONSUME_GROUP, TOPIC_NUM));
//		config.setTopic(TOPIC);
//		config.setQueueName(CONSUME_GROUP);
		config.setAutoAck(false);
		config.setPrefetchCount(1000);// 便于多消费者测试，设值小一点
		config.setConsumerNum((byte)1);
		config.setMaxWaitMillis(3000);
		config.setSkipNull(true);
//		Consumer consumer = new Consumer(config, new IConsume<Map<String, Object>>() {
//			public void consume(Map<String, Object> bean) {
//				System.err.println("消费数据：" + JSON.toJSONString(bean));
//			}
//		});
		Consumer consumer = new Consumer(config, (bean) -> {// TODO lambda表达式无法拦截
				System.err.println("消费数据：" + JSON.toJSONString(bean));
		});
//		Consumer consumer = new Consumer(config, 10, (beans) -> {
//				System.err.println("消费数据"+beans.size()+"条：" + JSON.toJSONString(beans));
//				return null;
//		});
//		Consumer consumer = new Consumer(config, 10, new IBatchConsume<Map<String, Object>>() {
//			public List<Map<String, Object>> consume(List<Map<String, Object>> beans) {
//				System.err.println("消费数据"+beans.size()+"条：" + JSON.toJSONString(beans));
//				return null;
//			}
//		});
		consumer.start();
		Thread.sleep(10000L);
		System.err.println("准备停止");
		consumer.shutdown();
		System.err.println("已停止......");
	}
	
	private static void setCommonConfig(BaseConfig config) {
		config.setAppName("ecpark-agent-test");
//		config.setServerAddress("10.1.1.205");
		config.setServerAddress("10.1.1.207");
		config.setUserName("admin");
		config.setPass("hlym3321");
		config.setSerializer(new StringSerializer());
//		config.setSerializer(new JsonSerializer(Map.class));
	}

}
