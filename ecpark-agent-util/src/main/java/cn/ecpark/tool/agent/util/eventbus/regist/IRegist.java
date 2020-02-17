package cn.ecpark.tool.agent.util.eventbus.regist;


import java.util.List;

import cn.ecpark.tool.agent.util.eventbus.consume.ConsumeConfig;

public interface IRegist {

    /**
     * 返回需加载的消费者
     * @return
     */
	List<? extends ConsumeConfig<?>> regist();
}
