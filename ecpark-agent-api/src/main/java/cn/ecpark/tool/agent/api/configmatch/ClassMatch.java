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
public class ClassMatch implements IConfigMatch {
	private String cls;
	
	public ClassMatch(String cls) {
		this.cls = cls;
	}
	
	@Override
	public boolean isMatch(MethodDefined methodDefined) {
		return methodDefined.getClassDefined().getClassName() != null && methodDefined.getClassDefined().getClassName().equals(cls);
	}
	
	@Override
	public String toString() {
		return cls;
	}
	
}
