package cn.ecpark.tool.agent.util.eventbus.regist;


import java.util.List;

import cn.ecpark.tool.agent.util.eventbus.consume.ConsumeFactory;

/**
 * 通过注册消费工厂实例，可以保证多线程消费时，每个实例仅被一个线程调用
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年10月11日 下午4:50:12
 */
public interface IConsumeFactoryRegist extends IRegist {

    /**
     * 返回需加载的消费者工厂实例
     * @return
     */
	@Override
	List<ConsumeFactory<?>> regist();
}
