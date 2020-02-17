package cn.ecpark.tool.agent.util.eventbus.regist;


import java.util.List;

import cn.ecpark.tool.agent.util.eventbus.consume.BaseConfigConsume;

public interface IConsumeRegist extends IRegist {

    /**
     * 返回需加载的消费者
     * @return
     */
	@Override
	List<BaseConfigConsume<?>> regist();
}
