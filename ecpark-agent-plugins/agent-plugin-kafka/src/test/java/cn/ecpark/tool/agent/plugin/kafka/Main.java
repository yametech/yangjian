package cn.ecpark.tool.agent.plugin.kafka;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.kafka.common.TopicPartition;

import com.alibaba.fastjson.JSON;

import cn.ecpark.plugin.mq.common.BaseConfig;
import cn.ecpark.plugin.mq.consumer.Consumer;
import cn.ecpark.plugin.mq.consumer.api.IConsume;
import cn.ecpark.plugin.mq.consumer.api.PartitionRebalanceListener;
import cn.ecpark.plugin.mq.consumer.config.KafkaConsumeConfig;
import cn.ecpark.plugin.mq.producer.Publisher;
import cn.ecpark.plugin.mq.producer.config.KafkaPublishConfig;
import cn.ecpark.plugin.mq.serialization.JsonSerializer;

public class Main {
	private static final String ADDRESS = "10.200.10.19:9094,10.1.1.232:9094,10.1.1.190:9094";
	private static final String USER_NAME = "";
	private static final String PASS = "";
	private static final String TOPIC = "ECPARK-AGENT-TOPIC";
	private static final int PARTITION_NUM = 1;
	private static final String CONSUME_GROUP = "ECPARK-AGENT-TOPIC-GROUP";
	private static final int PUBLISH_NUM = 10;
	private static final int CONSUME_MILLIS = 10000;
	
	public static void main(String[] args) throws InterruptedException {
		publish();
		//consume();
//		Thread.sleep(100000);
	}
	
//	@org.junit.Test
	public static void publish() throws InterruptedException {
		KafkaPublishConfig config = new KafkaPublishConfig();
		setCommonConfig(config);
		config.setTopic(TOPIC);
		config.setShardTopicNum(1);
		config.setNumPartitions(PARTITION_NUM);
		config.setReplicationFactor((short) 1);
//		config.setCompressionType("lz4");
		config.setSendAsync(true);
		Publisher publisher = new Publisher(config);
		for(int i = 0; i < PUBLISH_NUM; i++) {
			Map<String, Boolean> data = new HashMap<>();
			data.put("kafka", true);
			publisher.publish(i+"", data);
//			Thread.sleep(500);
		}
		System.err.println("完成发布" + PUBLISH_NUM + "条");
		publisher.shutdown();
	}
	
//	@org.junit.Test
	public static void consume() throws InterruptedException {
		KafkaConsumeConfig<String> config = new KafkaConsumeConfig<>();
		setCommonConfig(config);
		config.setTopics(Arrays.asList(TOPIC));
		config.setAutoAck(false);
		config.setConsumeGroup(CONSUME_GROUP);
		config.setConsumerNum((byte)2);
		config.setNumPartitions(PARTITION_NUM);
		config.setReplicationFactor((short) 1);
		config.setMaxWaitMillis(5000);
		config.setSkipNull(false);
		config.setFirstConsumePosition("earliest");// 首次订阅需要设置才能读取之前发布的数据
		config.setListener(new PartitionRebalanceListener() {
			@Override
			public void onPartitionsAssigned(Collection<TopicPartition> collection) {
				System.err.println(">>>>>>Assigned");
			}
			@Override
			public void onPartitionsRevoked(Collection<TopicPartition> collection) {
				System.err.println(">>>>>>Revoked");
			}
		});
		
		AtomicLong num = new AtomicLong(0);
		Consumer consumer = new Consumer(config, new IConsume<Map<String, Boolean>>() {
			public void consume(Map<String, Boolean> bean) {
				if(bean != null) {
					num.incrementAndGet();
				}
				System.err.println("消费数据：" + JSON.toJSONString(bean));
//				try {
//					Thread.sleep(10);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
			}
		});
//		Consumer consumer = new Consumer(config, 100, new IBatchConsume<String>() {
//			@Override
//			public List<String> consume(List<String> beans) {
//				if(beans != null) {
//					num.addAndGet(beans.size());
//				}
////				log.info("消费数据：" + JSON.toJSONString(beans));
//				return null;
//			}
//		});
		
//		Consumer<Map<String, Object>> consumer = new Consumer<>(config, new IConsume<Map<String, Object>>() {
//			public void consume(Map<String, Object> bean) {
//				log.info("消费数据：" + bean);
//			}
//		});
//		Consumer<Map<String, Object>> consumer = new Consumer<>(config, 6, new IBatchConsume<Map<String, Object>>() {
//			public List<Map<String, Object>> consume(List<Map<String, Object>> beans) {
//				log.info("消费数据{}条：{}",beans.size() , JSON.toJSONString(beans));
//				return null;
//			}
//		});
		consumer.start();
		Thread.sleep(CONSUME_MILLIS);
		System.err.println("准备停止");
		consumer.shutdown();
		System.err.println("已停止......");
	}
	
	private static void setCommonConfig(BaseConfig config) {
		config.setAppName("test-client-liuzz");
		config.setServerAddress(ADDRESS);
		config.setUserName(USER_NAME);
		config.setPass(PASS);
		config.setSerializer(new JsonSerializer(Map.class));
//		config.setSerializer(new StringSerializer());
	}
}
