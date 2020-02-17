package cn.ecpark.tool.agent.api.configmatch;

import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.bean.MethodDefined;

/**
 * 类注解匹配
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年11月7日 下午6:12:26
 */
public class ClassAnnotationMatch implements IConfigMatch {
	private String annotation;
	
	public ClassAnnotationMatch(String annotation) {
		this.annotation = annotation;
	}
	
	@Override
	public boolean isMatch(MethodDefined methodDefined) {
		return methodDefined.getClassDefined().getClassAnnotations() != null && methodDefined.getClassDefined().getClassAnnotations().contains(annotation);
	}
	
	@Override
	public String toString() {
		return annotation;
	}
	
}
