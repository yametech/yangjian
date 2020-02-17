package cn.ecpark.tool.agent.api.base;

import cn.ecpark.tool.agent.api.bean.MethodDefined;

public interface IMatch {
	
	/**
	 * 返回匹配配置
	 * @return
	 */
	IConfigMatch match();
	
	/**
	 * match匹配的方法会回调该方法
	 * @param methodInfo	方法定义完整路径
	 */
//	void method(String methodInfo);
	default void method(MethodDefined methodDefined) {}
	
	/**
	 * match匹配的方法会回调该方法
	 * @param className	类完整路径
	 * @param methodName	方法名
	 * @param agruments	参数
	 */
//	void method(String className, String methodName, String[] agruments);
}
