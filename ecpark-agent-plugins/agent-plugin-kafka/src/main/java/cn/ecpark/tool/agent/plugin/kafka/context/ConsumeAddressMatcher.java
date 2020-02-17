package cn.ecpark.tool.agent.plugin.kafka.context;

import java.util.Arrays;

import cn.ecpark.tool.agent.api.IEnhanceClassMatch;
import cn.ecpark.tool.agent.api.InterceptorMatcher;
import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.base.MethodType;
import cn.ecpark.tool.agent.api.bean.LoadClassKey;
import cn.ecpark.tool.agent.api.configmatch.ClassMatch;
import cn.ecpark.tool.agent.api.configmatch.CombineAndMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodArgumentIndexMatch;

/**
 * 增强类定义
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年11月8日 下午6:13:04
 */
public class ConsumeAddressMatcher implements InterceptorMatcher, IEnhanceClassMatch {
	
	@Override
	public IConfigMatch classMatch() {
		return new ClassMatch("org.apache.kafka.clients.consumer.KafkaConsumer");
	}
	
	@Override
	public IConfigMatch match() {
		return new CombineAndMatch(Arrays.asList(
				new ClassMatch("org.apache.kafka.clients.consumer.KafkaConsumer"),
//				new MethodNameMatch("KafkaPublisher"),
//				new MethodArgumentNumMatch(1),
				new MethodArgumentIndexMatch(0, "java.util.Properties")
			));
	}
	
	@Override
	public LoadClassKey loadClass(MethodType type) {
		return new LoadClassKey("cn.ecpark.tool.agent.plugin.kafka.context.ConsumeAddressInterceptor");
	}
	
}
