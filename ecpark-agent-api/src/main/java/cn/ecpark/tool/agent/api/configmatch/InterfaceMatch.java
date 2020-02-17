package cn.ecpark.tool.agent.api.configmatch;

import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.bean.MethodDefined;

/**
 * 是否继承了指定接口
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年11月7日 下午6:13:10
 */
public class InterfaceMatch implements IConfigMatch {
	private String interfaceName;
	
	public InterfaceMatch(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	
	@Override
	public boolean isMatch(MethodDefined methodDefined) {
		return methodDefined.getClassDefined().getInterfaces() != null && methodDefined.getClassDefined().getInterfaces().contains(interfaceName);
	}
	
	@Override
	public String toString() {
		return interfaceName;
	}
	
}
