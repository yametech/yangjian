package cn.ecpark.tool.agent.util.eventbus.consume;

public interface ConsumeFactory<T> extends ConsumeConfig<T> {
	
	/**
	 * 获取consume实例
	 * @return
	 */
	BaseConsume<T> getConsume();
}
