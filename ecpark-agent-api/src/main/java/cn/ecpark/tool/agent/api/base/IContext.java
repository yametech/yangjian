package cn.ecpark.tool.agent.api.base;

public interface IContext {
	
	/**
	 * 获取上下文数据
	 * @param key
	 * @return
	 */
	Object _getAgentContext(String key);
//	Object _getAgentContext();
	
	/**
	 * 设置上下文
	 * @param key
	 * @param value
	 * @return
	 */
	void _setAgentContext(String key, Object value);
//	void _setAgentContext(Object value);
}
