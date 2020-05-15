/*
 * Copyright 2020 yametech.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yametech.yangjian.agent.core.core.elementmatch;

import com.yametech.yangjian.agent.api.bean.Annotation;
import com.yametech.yangjian.agent.api.bean.ClassDefined;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;

import java.util.*;
import java.util.stream.Collectors;

public class ElementMatcherConvert {
    
	private ElementMatcherConvert() {}

//	public static ClassDefined convert(TypeDescription typeDescription) {
//		return convert(typeDescription, false);
//	}
    public static ClassDefined convert(TypeDescription typeDescription) {
		ClassDefined classDefined = new ClassDefined(getInterface(typeDescription), getSuperClass(typeDescription), getClassAnnotation(typeDescription), typeDescription.getActualName());
//		if(!containsMethod) {
//			return classDefined;
//		}
		List<MethodDefined> methodDefinedList = new ArrayList<>();
		for(MethodDescription.InDefinedShape inDefinedShape : typeDescription.getDeclaredMethods()) {
			methodDefinedList.add(convert(classDefined, inDefinedShape));
		}
		classDefined.setMethods(methodDefinedList);
		return classDefined;
    }

	public static MethodDefined convert(MethodDescription.InDefinedShape inDefinedShape) {
		TypeDescription thisClass = inDefinedShape.getDeclaringType().asErasure();
		ClassDefined classDefined = convert(thisClass);
		return convert(classDefined, inDefinedShape);
	}

	/**
	 * InDefinedShape转换为MethodDefined
	 * @param inDefinedShape
	 * @return
	 */
	public static MethodDefined convert(ClassDefined classDefined, MethodDescription.InDefinedShape inDefinedShape) {
		TypeDescription thisClass = inDefinedShape.getDeclaringType().asErasure();
		String methodName = inDefinedShape.getInternalName();
		if(!inDefinedShape.isMethod()) {
			String className = thisClass.getActualName();
			int beginIndex = className.lastIndexOf(".") + 1;
			methodName = className.substring(beginIndex);
		}
//    	String methodName = inDefinedShape.isMethod() ? inDefinedShape.getInternalName() : inDefinedShape.getDeclaringType().asErasure().getName();
		return new MethodDefined(classDefined, getMethodAnnotation(inDefinedShape), inDefinedShape.toString(),
				methodName, getParams(inDefinedShape), inDefinedShape.getReturnType().asErasure().getActualName(),
				inDefinedShape.isStatic(), inDefinedShape.isConstructor(), inDefinedShape.isMethod());

//    	System.out.println("class=" + thisClass.getActualName());
//    	System.out.println("SuperClass=" + getSuperClass(thisClass));
//    	try {
//    		System.out.println("interface=" + getInterface(thisClass));
//    	} catch(Exception e) {
//    		e.printStackTrace();
//    	}
//    	return null;
//    	System.out.println("methodDes=" + inDefinedShape.toString());
//        System.out.println("return=" + inDefinedShape.getReturnType().asErasure().getActualName());
//        System.out.println("method=" + (inDefinedShape.isMethod() ? inDefinedShape.getInternalName() : inDefinedShape.getDeclaringType().asErasure().getName()));
//        inDefinedShape.getParameters().asTypeList().asErasures().forEach(defined -> System.out.println("Parameter=" + defined.getActualName()));
//        inDefinedShape.getDeclaredAnnotations().asTypeList().forEach(anno ->  System.out.println("annotation=" + anno.getActualName()));
	}
    
    public static String[] getParams(MethodDescription.InDefinedShape inDefinedShape) {
    	return inDefinedShape.getParameters().asTypeList().asErasures().stream()
    			.map(TypeDescription::getActualName)
    			.toArray(String[]::new);
    }
    
    private static Set<Annotation> getMethodAnnotation(MethodDescription.InDefinedShape inDefinedShape) {
		return inDefinedShape.getDeclaredAnnotations().stream().map(annotation -> {
					Map<String, Object> values = new HashMap<>();
					annotation.getAnnotationType().getDeclaredMethods().forEach(method -> values.put(method.getActualName(), annotation.getValue(method).resolve()));
					return new Annotation(annotation.getAnnotationType().getActualName(), values);
				}).collect(Collectors.toSet());
    }
    
    public static Set<Annotation> getClassAnnotation(TypeDescription thisClass) {
		return thisClass.getDeclaredAnnotations().stream().map(annotation -> {
					Map<String, Object> values = new HashMap<>();
					annotation.getAnnotationType().getDeclaredMethods().forEach(method -> values.put(method.getActualName(), annotation.getValue(method).resolve()));
					return new Annotation(annotation.getAnnotationType().getActualName(), values);
				}).collect(Collectors.toSet());
    }

    /**
     * 获取类的父类及祖类，不包含Object
     * @param cls
     * @return
     */
    public static Set<String> getSuperClass(TypeDescription cls) {
    	Set<String> clsList = new HashSet<>();
    	while(cls.getSuperClass() != null) {
    		String superCls = cls.getSuperClass().asErasure().getActualName();
    		if("java.lang.Object".equals(superCls)) {
    			break;
    		}
    		clsList.add(superCls);
    		cls = cls.getSuperClass().asErasure();
        }
    	return clsList;
    }
    
    /**
     * 获取类接口，包含直接接口和间接接口（间接接口为父类继承的接口）
     * @param cls
     * @return
     */
    public static Set<String> getInterface(TypeDescription cls) {
    	Set<String> interfaces = new HashSet<>();
    	while(cls != null) {
    		if("java.lang.Object".equals(cls.getActualName())) {
    			break;
    		}
    		interfaces.addAll(getClassInterface(cls));
    		if(cls.getSuperClass() == null) {
    			break;
    		}
    		cls = cls.getSuperClass().asErasure();
        }
    	return interfaces;
    }
    
    private static Set<String> getClassInterface(TypeDescription cls) {
    	Set<String> clsList = new HashSet<>();
    	if(cls.getInterfaces() == null || cls.getInterfaces().isEmpty()) {
    		return clsList;
    	}
    	cls.getInterfaces().asErasures().forEach(inter -> {
			clsList.add(inter.getName());
			clsList.addAll(getClassInterface(inter));
    	});
    	return clsList;
    }
}
