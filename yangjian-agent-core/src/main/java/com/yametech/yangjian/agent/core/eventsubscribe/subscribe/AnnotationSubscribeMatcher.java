package com.yametech.yangjian.agent.core.eventsubscribe.subscribe;

import com.yametech.yangjian.agent.api.InterceptorMatcher;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.bean.MethodDefined;
import com.yametech.yangjian.agent.api.common.InstanceManage;
import com.yametech.yangjian.agent.api.common.MethodUtil;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.api.configmatch.*;
import com.yametech.yangjian.agent.api.log.ILogger;
import com.yametech.yangjian.agent.api.log.LoggerFactory;
import com.yametech.yangjian.agent.core.YMAgent;
import com.yametech.yangjian.agent.core.eventsubscribe.event.EventMatcher;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class AnnotationSubscribeMatcher implements InterceptorMatcher {
    private static final ILogger LOG = LoggerFactory.getLogger(AnnotationSubscribeMatcher.class);
    private static final String AUTO_INSTANCE_NAME = "com.yametech.yangjian.agent.client.annotation.AutoInstance";
    private static final String SUBSCRIBE_ANNOTATION_NAME = "com.yametech.yangjian.agent.client.annotation.Subscribe";
    private static final String SUBSCRIBES_ANNOTATION_NAME = "com.yametech.yangjian.agent.client.annotation.Subscribes";
    private static final Map<String, Boolean> LOADED_CLASS = new ConcurrentHashMap<>();
    private static final int CLASS_MAX_NUMBER = 500;

    @Override
    public IConfigMatch match() {
        return new MethodAnnotationConstructorMatch("com.yametech.yangjian.agent.client.annotation.Subscribe");
    }

    @Override
    public void method(MethodDefined methodDefined) {
        String className = methodDefined.getClassDefined().getClassName();
        if(LOADED_CLASS.size() > CLASS_MAX_NUMBER) {
            LOG.warn("注册事件监听类过多（最大{}个）,忽略订阅方法注册：{}", CLASS_MAX_NUMBER, methodDefined.getMethodDes());
            return;
        }
        LOADED_CLASS.computeIfAbsent(className, key -> {
            methodDefined.getClassDefined().getMethods().forEach(md -> {
                String eventGroup = MethodUtil.getId(md);
                md.getMethodAnnotations().stream()
                        .filter(annotation -> SUBSCRIBE_ANNOTATION_NAME.equals(annotation.getName()) || SUBSCRIBES_ANNOTATION_NAME.equals(annotation.getName()))
//                        .flatMap(md -> md.getMethodAnnotations() == null ? Stream.empty() : md.getMethodAnnotations().stream())
                        .flatMap(annotation -> {
                            if(SUBSCRIBES_ANNOTATION_NAME.equals(annotation.getName())) {
                                return Arrays.stream((com.yametech.yangjian.agent.api.bean.Annotation[]) annotation.getMethodValues().get("value"));
                            } else {
                                return Stream.of(annotation);
                            }
                        })
                        .forEach(annotation -> {
                            String[] interfaces = (String[]) annotation.getMethodValues().get("interfaces");
                            String parent = (String) annotation.getMethodValues().get("parent");
                            String clsName = (String) annotation.getMethodValues().get("className");
                            String methodName = (String) annotation.getMethodValues().get("methodName");
                            int argumentNumber = (int) annotation.getMethodValues().get("argumentNumber");
                            String[] argumentType = (String[]) annotation.getMethodValues().get("argumentType");
//                            boolean ignoreParams = (boolean) annotation.getMethodValues().get("ignoreParams");
                            IConfigMatch match = getConfigMatch(interfaces, parent, clsName, methodName, argumentNumber, argumentType);
                            if(match == null) {
                                LOG.warn("{}注解Subscribe的值配置错误(必须包含至少一个非空配置值，interfaces与parent必须至少配置一个)", className);
                                return;
                            }
                            EventMatcher matcher = new EventMatcher(eventGroup, match);
                            InstanceManage.registry(matcher);
                            YMAgent.addTransformerMatchers(matcher);
                            LOG.info("addTransformerMatchers：{} - {}", eventGroup, matcher.match());
                        });
            });
            return true;
        });
    }

    private IConfigMatch getConfigMatch(String[] interfaces, String parent, String className, String methodName, int argumentNumber, String[] argumentType) {
        boolean containsInterface = false;
        List<IConfigMatch> matches = new ArrayList<>();
        if(interfaces.length > 0) {
            List<IConfigMatch> interfacesMatches = new ArrayList<>();
            for(String inter : interfaces) {
                if(StringUtil.notEmpty(inter.trim())) {
                    interfacesMatches.add(new InterfaceMatch(inter.trim()));
                }
            }
            if(interfacesMatches.size() > 0) {
                matches.add(new CombineOrMatch(interfacesMatches));
                containsInterface = true;
            }
        }
        boolean containsParent = false;
        if(StringUtil.notEmpty(parent.trim())) {
            matches.add(new SuperClassMatch(parent.trim()));
            containsParent = true;
        }
        boolean containsClassName = false;
        if(StringUtil.notEmpty(className.trim())) {
            matches.add(new ClassMatch(className.trim()));
            containsClassName = true;
        }
        if(StringUtil.notEmpty(methodName.trim())) {
            matches.add(new MethodNameMatch(methodName.trim()));
        }
        if(argumentNumber != -1) {
            matches.add(new MethodArgumentNumMatch(argumentNumber));
        }

        for (int i = 0; i < argumentType.length; i++) {
            String type = argumentType[i];
            if(StringUtil.isEmpty(type)) {
                continue;
            }
            matches.add(new MethodArgumentIndexMatch(i, type));
        }
        if(matches.size() == 0 || !(containsInterface || containsParent || containsClassName)) {
            return null;
        }
        return new CombineAndMatch(matches);
    }

    @Override
    public void onComplete(String typeName, ClassLoader classLoader, boolean loaded) {
        try {
            Class<?> cls = Class.forName(typeName, true, classLoader);
            for (Annotation annotation : cls.getAnnotations()) {
                if(AUTO_INSTANCE_NAME.equals(annotation.annotationType().getName())) {
                    LOG.info("create instance: {}，{}，{}", typeName, classLoader, loaded);
                    cls.newInstance();// 主动创建实例
                    return;
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            LOG.warn(e, "create instance exception");
        }
    }

    @Override
    public LoadClassKey loadClass(MethodType type, MethodDefined methodDefined) {
        return new LoadClassKey(AnnotationSubscribeInterceptor.class.getName());// AnnotationSubscribeInterceptor是线程安全的，所以此处单实例没问题
    }
}