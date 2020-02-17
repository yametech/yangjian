package cn.ecpark.tool.agent.core.core.agent;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.ecpark.tool.agent.api.IEnhanceClassMatch;
import cn.ecpark.tool.agent.api.InterceptorMatcher;
import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.base.IContext;
import cn.ecpark.tool.agent.api.base.IInterceptorInit;
import cn.ecpark.tool.agent.api.base.IMatch;
import cn.ecpark.tool.agent.api.base.MethodType;
import cn.ecpark.tool.agent.api.base.SPI;
import cn.ecpark.tool.agent.api.bean.ClassDefined;
import cn.ecpark.tool.agent.api.bean.LoadClassKey;
import cn.ecpark.tool.agent.api.bean.MethodDefined;
import cn.ecpark.tool.agent.api.interceptor.IConstructorListener;
import cn.ecpark.tool.agent.api.interceptor.IMethodAOP;
import cn.ecpark.tool.agent.api.interceptor.IStaticMethodAOP;
import cn.ecpark.tool.agent.core.core.InstanceManage;
import cn.ecpark.tool.agent.core.core.classloader.InterceptorInstanceLoader;
import cn.ecpark.tool.agent.core.core.elementmatch.ElementMatcherConvert;
import cn.ecpark.tool.agent.core.core.elementmatch.MethodElementMatcher;
import cn.ecpark.tool.agent.core.core.interceptor.ContextInterceptor;
import cn.ecpark.tool.agent.core.core.interceptor.YmInstanceConstructorInterceptor;
import cn.ecpark.tool.agent.core.core.interceptor.YmInstanceInterceptor;
import cn.ecpark.tool.agent.core.core.interceptor.YmStaticInterceptor;
import cn.ecpark.tool.agent.core.exception.AgentPackageNotFoundException;
import cn.ecpark.tool.agent.core.log.ILogger;
import cn.ecpark.tool.agent.core.log.LoggerFactory;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

public class AgentTransformer implements AgentBuilder.Transformer {
	public static final String OBJECT_CONTEXT_FIELD_NAME = "__context_field__";// 继承Icontext接口的对象全局变量名称
	private static ILogger log = LoggerFactory.getLogger(AgentTransformer.class);
    private List<InterceptorMatcher> interceptorMatchers;
    private List<IEnhanceClassMatch> classMatches;
    private ElementMatcher<? super MethodDescription> notMatches;

	public AgentTransformer(List<InterceptorMatcher> interceptorMatchers, 
			IConfigMatch ignoreMethods, List<IEnhanceClassMatch> classMatches) {
        this.interceptorMatchers = interceptorMatchers;
        if(ignoreMethods != null) {
            notMatches = new MethodElementMatcher(ignoreMethods, "method_ignore");
        }
        this.classMatches = classMatches;
    }

    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, 
    		TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule) {
//    	log.info("{}:transform", typeDescription);
    	if(typeDescription.isInterface()) {
    		return builder;
    	}
//    	log.info("{}:enhanceContextClass", typeDescription);
    	builder = enhanceContextClass(typeDescription, builder);
//    	if(typeDescription.toString().indexOf("RabbitmqService") != -1) {
//    		System.err.println(">>>>>>>>>>>>>");
//    	}
//    	log.info("{}:enhanceMethod", typeDescription);
    	builder = enhanceMethod(typeDescription, builder, classLoader);
        return builder;
//        return builder.method(ElementMatchers.not(new MethodElementMatcher(Config.getIgnoreMethods())))
//                .intercept(MethodDelegation.to(new TimingInterceptor()));
    }
    
    /**
     * 增强方法
     * @param typeDescription
     * @param builder
     * @return
     */
    private DynamicType.Builder<?> enhanceMethod(TypeDescription typeDescription, DynamicType.Builder<?> builder, ClassLoader classLoader) {
    	List<IMatch> matches = InstanceManage.listSpiInstance(IMatch.class);
//    	log.info("{}:matches", typeDescription);
        // builder针对同一个类方法设置多次intercept，仅最后一次生效，所以多个MethodInterceptor拦截同一个方法时需合并到一个intercept中
        for(MethodDescription.InDefinedShape inDefinedShape : typeDescription.getDeclaredMethods()) {
            MethodDefined methodDefined = ElementMatcherConvert.convert(inDefinedShape);
            matches.stream().filter(match -> match.match() != null && match.match().isMatch(methodDefined))
            	.forEach(match -> match.method(methodDefined));
            MethodType type = getMethodType(inDefinedShape);
//            log.info("{}:for begin", inDefinedShape);
//    		List<InterceptorMatcher> interceptors = interceptorMatchers.stream()
            List<Object> interceptors = interceptorMatchers.stream()
    			.filter(aop -> aop.match() != null && aop.match().isMatch(methodDefined))
    			.map(matcher -> {
//    				log.info("{}:map enter", inDefinedShape);
    				LoadClassKey loadClass = matcher.loadClass(type);
    				if(loadClass == null) {
    					return null;
    				}
					try {
						Object obj = InterceptorInstanceLoader.load(loadClass.getKey(), loadClass.getCls(), classLoader);
						if(obj instanceof SPI) {
							throw new IllegalStateException("不能实现SPI接口");
						}
//						log.info("{}:map load", inDefinedShape);
						if(matcher instanceof IInterceptorInit) {
							((IInterceptorInit)matcher).init(obj, classLoader, type);
						}
//						log.info("{}:map init", inDefinedShape);
						log.debug("classLoader:{}	{}	{}	{}", obj, classLoader, loadClass, inDefinedShape);
						return obj;
					} catch (IllegalAccessException | InstantiationException | ClassNotFoundException
							| AgentPackageNotFoundException e) {
						log.warn(e, "加载实例异常{}", loadClass);
						return null;
					}
				}).filter(interceptor -> {
    				return interceptor != null && (
        						(inDefinedShape.isStatic() && interceptor instanceof IStaticMethodAOP) || 
        						(inDefinedShape.isConstructor() && interceptor instanceof IConstructorListener) || 
        						(inDefinedShape.isMethod() && interceptor instanceof IMethodAOP)
    						);
    				
    			}).collect(Collectors.toList());
    		if(interceptors == null || interceptors.isEmpty()) {
    			continue;
    		}
//    		log.info("{}:for builder", inDefinedShape);
    		log.debug("match method:{}", inDefinedShape);
            if(inDefinedShape.isStatic()) {// 静态方法
        		builder = builder.method(getMethodMatch(inDefinedShape))
        				.intercept(MethodDelegation.withDefaultConfiguration()
        						.to(new YmStaticInterceptor(interceptors.stream().toArray(IStaticMethodAOP[]::new))));
//        						.to(new YmStaticInterceptor(interceptors, classLoader, inDefinedShape)));
            } else if(inDefinedShape.isConstructor()) {// 构造方法
        		builder = builder.constructor(getMethodMatch(inDefinedShape))
        				.intercept(SuperMethodCall.INSTANCE.andThen(MethodDelegation.withDefaultConfiguration()
            					.to(new YmInstanceConstructorInterceptor(interceptors.stream().toArray(IConstructorListener[]::new)))));
//        						.to(new YmInstanceConstructorInterceptor(interceptors, classLoader, inDefinedShape))));
            } else if(inDefinedShape.isMethod()) {// 实例方法
        		builder = builder.method(getMethodMatch(inDefinedShape))
        				.intercept(MethodDelegation.withDefaultConfiguration()
        						.to(new YmInstanceInterceptor(interceptors.stream().toArray(IMethodAOP[]::new))));
//        						.to(new YmInstanceInterceptor(interceptors, classLoader, inDefinedShape)));
            }
//            log.info("{}:for done", inDefinedShape);
        }
//        log.info("{}:for exit", typeDescription);
        return builder;
    }
    
    private MethodType getMethodType(MethodDescription.InDefinedShape inDefinedShape) {
    	if(inDefinedShape.isStatic()) {
    		return MethodType.STATIC;
    	} else if(inDefinedShape.isConstructor()) {
    		return MethodType.CONSTRUCT;
    	} else if(inDefinedShape.isMethod()) {
    		return MethodType.INSTANCE;
    	}
    	return null;
    }
    
    private ElementMatcher<? super MethodDescription> getMethodMatch(MethodDescription.InDefinedShape inDefinedShape) {
    	return notMatches == null ? ElementMatchers.is(inDefinedShape) : 
        	ElementMatchers.not(notMatches).and(ElementMatchers.is(inDefinedShape));
    }
    
    /**
     * 增强class，继承IContext
     * @param typeDescription
     * @param builder
     * @return
     */
    private DynamicType.Builder<?> enhanceContextClass(TypeDescription typeDescription, DynamicType.Builder<?> builder) {
    	if(classMatches == null || classMatches.isEmpty()) {
    		return builder;
    	}
//    	log.info("{}:Defined", typeDescription);
    	ClassDefined classDefined = ElementMatcherConvert.convert(typeDescription);
    	MethodDefined methodDefined = new MethodDefined(classDefined, null, null, null, null, null);
    	boolean match = classMatches.stream().anyMatch(aop -> aop.classMatch() != null && aop.classMatch().isMatch(methodDefined));
    	if(!match) {
    		return builder;
    	}
//    	log.info("{}:builder", typeDescription);
    	// 将context放入对象实例中，好处：生命周期与对象实例一致、取值时间复杂度为O(1)，经过测试大量创建对象时，这种方式耗时更少
    	return builder.defineField(OBJECT_CONTEXT_FIELD_NAME, Map.class, Opcodes.ACC_PRIVATE | Opcodes.ACC_VOLATILE)
    			.implement(IContextField.class)
    			.intercept(FieldAccessor.ofField(OBJECT_CONTEXT_FIELD_NAME))
    			.implement(IContext.class)
    			.method(ElementMatchers.named("_getAgentContext").or(ElementMatchers.named("_setAgentContext")))
    			.intercept(MethodDelegation.to(ContextInterceptor.class));
    	// 将context放入静态Map中，维护对象实例与context的关系，好处：少实现一个接口及类字段
//    	return builder.defineField(ContextConstants.OBJECT_CONTEXT_FIELD_NAME, Map.class, Opcodes.ACC_PRIVATE)
//    			.implement(IContext.class)
//    			.method(ElementMatchers.named("_getAgentContext").or(ElementMatchers.named("_setAgentContext")))
//    			.intercept(MethodDelegation.to(ContextMapInterceptor.class));
    }

}
