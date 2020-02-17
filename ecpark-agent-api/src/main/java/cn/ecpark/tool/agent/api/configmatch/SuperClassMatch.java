package cn.ecpark.tool.agent.api.configmatch;

import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.bean.MethodDefined;

/**
 * 继承类匹配（可以是父类或间接父类）
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年11月7日 下午6:12:26
 */
public class SuperClassMatch implements IConfigMatch {
	private String superClass;
	
	public SuperClassMatch(String superClass) {
		this.superClass = superClass;
	}
	
	@Override
	public boolean isMatch(MethodDefined methodDefined) {
		return methodDefined.getClassDefined().getParents() != null && methodDefined.getClassDefined().getParents().contains(superClass);
	}
	
	@Override
	public String toString() {
		return superClass;
	}
	
}
