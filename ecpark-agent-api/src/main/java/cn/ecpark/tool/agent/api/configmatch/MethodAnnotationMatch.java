package cn.ecpark.tool.agent.api.configmatch;

import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.bean.MethodDefined;

/**
 * 方法注解匹配
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年11月7日 下午6:13:46
 */
public class MethodAnnotationMatch implements IConfigMatch {
	private String annotation;
	
	public MethodAnnotationMatch(String annotation) {
		this.annotation = annotation;
	}
	
	@Override
	public boolean isMatch(MethodDefined methodDefined) {
		return methodDefined.getMethodAnnotations() != null && methodDefined.getMethodAnnotations().contains(annotation);
	}
	
	@Override
	public String toString() {
		return annotation;
	}
	
}
