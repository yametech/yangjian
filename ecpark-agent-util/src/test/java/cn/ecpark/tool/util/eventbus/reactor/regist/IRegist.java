package cn.ecpark.tool.util.eventbus.reactor.regist;


import java.util.List;

import cn.ecpark.tool.util.eventbus.reactor.consume.BaseConsume;

public interface IRegist {

    /**
     * 返回需加载的消费者
     * @return
     */
    List<BaseConsume<Object>> regist();
}
