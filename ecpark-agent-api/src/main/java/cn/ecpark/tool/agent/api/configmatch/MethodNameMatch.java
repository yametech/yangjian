package cn.ecpark.tool.agent.api.configmatch;

import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.bean.MethodDefined;

/**
 * 方法名称匹配
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年11月7日 下午6:12:45
 */
public class MethodNameMatch implements IConfigMatch {
	private String name;
	
	public MethodNameMatch(String name) {
		this.name = name;
	}
	
	@Override
	public boolean isMatch(MethodDefined methodDefined) {
		return methodDefined.getMethodName() != null && methodDefined.getMethodName().equals(name);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
