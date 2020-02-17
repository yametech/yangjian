package cn.ecpark.tool.agent.api.bean;

import java.util.Set;

public class MethodDefined {
	private ClassDefined classDefined;// 类定义
	private String methodDes;// 方法描述
	private Set<String> methodAnnotations;// 方法注解
	private String methodName;// 方法名
	private String[] params;// 方法参数类型
	private String methodRet;// 方法返回值类型

	public MethodDefined(ClassDefined classDefined, Set<String> methodAnnotations, 
			String methodDes, String methodName, String[] params, String methodRet) {
		this.classDefined = classDefined;
		this.methodDes = methodDes;
		this.methodAnnotations = methodAnnotations;
		this.methodName = methodName;
		this.params = params;
		this.methodRet = methodRet;
	}

	public ClassDefined getClassDefined() {
		return classDefined;
	}
	
	public Set<String> getMethodAnnotations() {
		return methodAnnotations;
	}
	
	public String getMethodDes() {
		return methodDes;
	}

	public String getMethodName() {
		return methodName;
	}

	public String[] getParams() {
		return params;
	}

	public String getMethodRet() {
		return methodRet;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString()).append('\t').append(methodDes);
		if(methodAnnotations != null && !methodAnnotations.isEmpty()) {
			builder.append(" annotations ");
			methodAnnotations.forEach(cls -> builder.append(cls).append(','));
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder.toString();
	}
	
}
