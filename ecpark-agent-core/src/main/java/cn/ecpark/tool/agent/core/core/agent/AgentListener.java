package cn.ecpark.tool.agent.core.core.agent;

import java.util.Set;

import cn.ecpark.tool.agent.core.core.elementmatch.ElementMatcherConvert;
import cn.ecpark.tool.agent.core.log.ILogger;
import cn.ecpark.tool.agent.core.log.LoggerFactory;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;

public class AgentListener implements AgentBuilder.Listener {
	private static ILogger log = LoggerFactory.getLogger(AgentListener.class);
	
    @Override
    public void onDiscovery(String s, ClassLoader classLoader, JavaModule javaModule, boolean b) {

    }

    @Override
    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean b, DynamicType dynamicType) {
    	Set<String> superClasses = ElementMatcherConvert.getSuperClass(typeDescription);
    	Set<String> interfaces = ElementMatcherConvert.getInterface(typeDescription);
    	Set<String> annotations = ElementMatcherConvert.getClassAnnotation(typeDescription);
    	StringBuilder builder = new StringBuilder();
    	if(superClasses != null && !superClasses.isEmpty()) {
    		builder.append(" extends ");
    		superClasses.forEach(cls -> builder.append(cls).append(','));
    		builder.deleteCharAt(builder.length() - 1);
    	}
    	if(interfaces != null && !interfaces.isEmpty()) {
    		builder.append(" implements ");
    		interfaces.forEach(cls -> builder.append(cls).append(','));
    		builder.deleteCharAt(builder.length() - 1);
    	}
    	if(annotations != null && !annotations.isEmpty()) {
    		builder.append(" annotations ");
    		annotations.forEach(cls -> builder.append(cls).append(','));
    		builder.deleteCharAt(builder.length() - 1);
    	}
    	
    	log.info("Transformation:{} -> {}", typeDescription.getActualName(), builder.toString());
//        System.out.println("Transformation.getCanonicalName():" + typeDescription.getCanonicalName());
//        System.out.println("Transformation.getSimpleName:" + typeDescription.getSimpleName());
//        System.out.println("Transformation.getActualName:" + typeDescription.getActualName());
//        System.out.println("Transformation.getDescriptor:" + typeDescription.getDescriptor());
//        System.out.println("Transformation.getGenericSignature:" + typeDescription.getGenericSignature());
//        System.out.println("Transformation.getInternalName:" + typeDescription.getInternalName());
//        System.out.println("Transformation.getName:" + typeDescription.getName());
//        System.out.println("Transformation.getTypeName:" + typeDescription.getTypeName());
//        System.out.println("Transformation.getClass:" + typeDescription.getClass());

    }

    @Override
    public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean b) {
    }

    @Override
    public void onError(String s, ClassLoader classLoader, JavaModule javaModule, boolean b, Throwable throwable) {

    }

    @Override
    public void onComplete(String s, ClassLoader classLoader, JavaModule javaModule, boolean b) {
//    	try {
//    		if("com.alibaba.dubbo.rpc.proxy.InvokerInvocationHandler".equals(s)) {
//    			System.err.println("classLoader =====" + classLoader);
//    			System.err.println("currentThread classLoader =====" + Thread.currentThread().getContextClassLoader());
////    			Class<?> cls = classLoader.loadClass(s);
//    			Class<?> cls = Class.forName(s, false, classLoader);
//    			List<Class<?>> argCls = new ArrayList<Class<?>>();
//    			argCls.add(Class.forName("java.lang.Object"));
//    			argCls.add(Class.forName("java.lang.reflect.Method"));
//    			argCls.add(Class.forName("[Ljava.lang.Object;"));
//    			System.out.println(cls.getMethod("invoke", argCls.toArray(new Class[0])));
//    			System.out.println("<<<<<<<<<<<<<<<<<<<<<<" + cls);
//    		}
//    		
//    	} catch (Throwable e) {
//		}
    }
}
