package cn.ecpark.tool.agent.api.common;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.ecpark.tool.agent.api.bean.MethodDefined;
import cn.ecpark.tool.agent.api.bean.MethodInfo;

public class MethodUtil {
	private static Map<Method, String> cacheMethod = new ConcurrentHashMap<>();
	private static Map<MethodInfo, String> cacheMethodInfo = new ConcurrentHashMap<>();
	
	public static String getId(MethodDefined methodDefined) {
		return getId(methodDefined.getClassDefined().getClassName(), methodDefined.getMethodName(), methodDefined.getParams());
	}
	
	public static String getId(Method method) {
		if(method == null) {
			return null;
		}
		return getId(method.getDeclaringClass().getTypeName(), method.getName(), method.getParameterTypes());
	}
	
	public static String getId(MethodInfo methodInfo) {
		if(methodInfo == null) {
			return null;
		}
		return getId(methodInfo.getCls().getTypeName(), methodInfo.getMethodName(), methodInfo.getParameterTypes());
	}

	public static String getId(String className, String methodName, Class<?>[] parameterTypes) {
		String[] parameters = null;
		if(parameterTypes != null) {
			parameters = new String[parameterTypes.length];
			for(int i = 0; i < parameterTypes.length; i++) {
				parameters[i] = parameterTypes[i].getTypeName();
			}
		}
		return getId(className, methodName, parameters);
	}
	public static String getId(String className, String methodName, String[] parameters) {
		StringBuilder id = new StringBuilder();
		id.append(className).append('.').append(methodName).append('(');
		if(parameters != null) {
			for (int j = 0; j < parameters.length; j++) {
				id.append(parameters[j]);
				if (j < (parameters.length - 1)) {
					id.append(',');
				}
			}
		}
		return id.append(')').toString();
	}
	
//	public static String getId(InDefinedShape inDefinedShape) {
//		if(inDefinedShape == null) {
//			return null;
//		}
//		StringBuilder id = new StringBuilder();
//		id.append(inDefinedShape.getDeclaringType().asErasure().getActualName()).append('.')
//			.append(inDefinedShape.getName()).append('(');
//		TypeList parameterTypes = inDefinedShape.getParameters().asTypeList().asErasures();
//		for (int j = 0; j < parameterTypes.size(); j++) {
//			id.append(parameterTypes.get(j).getActualName());
//            if (j < (parameterTypes.size() - 1)) {
//            	id.append(',');
//            }
//        }
//		id.append(')');
//		return id.toString();
//	}
	
	/**
	 * 根据方法实例以及匹配条件获取匹配的methodId
	 * @param method
	 * @return
	 */
	public static String getCacheMethodId(Method method) {
		String methodId = cacheMethod.get(method);
		if(methodId != null) {
			return methodId;
		}
		methodId = getId(method);
		if(methodId != null) {
			cacheMethod.put(method, methodId);
		}
		return methodId;
	}
	
	public static String getCacheMethodId(Class<?> cls, String methodName, Class<?>[] parameterTypes) {
		MethodInfo methodInfo = new MethodInfo(cls, methodName, parameterTypes);
		String methodId = cacheMethodInfo.get(methodInfo);
		if(methodId != null) {
			return methodId;
		}
		methodId = getId(methodInfo);
//		if(matchMethodIds != null && !matchMethodIds.contains(methodId)) {
//			return null;
//		}
		if(methodId != null) {
			cacheMethodInfo.put(methodInfo, methodId);
		}
		return methodId;
	}
	
}
