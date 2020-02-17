package cn.ecpark.tool.agent.api.base;

public interface IWeight {
	
	/**
	 * 权重高的先执行
     * @return
     */
    default int weight() {
        return 0;
    }
}
