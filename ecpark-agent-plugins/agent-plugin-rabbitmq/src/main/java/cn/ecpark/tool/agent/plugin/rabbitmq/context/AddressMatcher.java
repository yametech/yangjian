package cn.ecpark.tool.agent.plugin.rabbitmq.context;

import java.util.Arrays;

import cn.ecpark.tool.agent.api.IEnhanceClassMatch;
import cn.ecpark.tool.agent.api.InterceptorMatcher;
import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.base.MethodType;
import cn.ecpark.tool.agent.api.bean.LoadClassKey;
import cn.ecpark.tool.agent.api.configmatch.CombineAndMatch;
import cn.ecpark.tool.agent.api.configmatch.CombineOrMatch;
import cn.ecpark.tool.agent.api.configmatch.InterfaceMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodNameMatch;
import cn.ecpark.tool.agent.api.configmatch.MethodReturnMatch;

/**
 * 添加地址上下文
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年11月8日 下午6:13:04
 */
public class AddressMatcher implements InterceptorMatcher, IEnhanceClassMatch {
	
	@Override
	public IConfigMatch classMatch() {
		return new CombineOrMatch(Arrays.asList(
				new InterfaceMatch("com.rabbitmq.client.Channel")
//				new ClassMatch("com.rabbitmq.client.impl.recovery.AutorecoveringChannel"),
//				new ClassMatch("com.rabbitmq.client.impl.ChannelN")
			));
	}
	
	@Override
	public IConfigMatch match() {
		return new CombineAndMatch(Arrays.asList(
				new InterfaceMatch("com.rabbitmq.client.Connection"),
				new MethodNameMatch("createChannel"),
				new MethodReturnMatch("com.rabbitmq.client.Channel")
			));
	}
	
	@Override
	public LoadClassKey loadClass(MethodType type) {
		return new LoadClassKey("cn.ecpark.tool.agent.plugin.rabbitmq.context.AddressInterceptor");
	}
	
}
