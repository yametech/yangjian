package com.yametech.yangjian.agent.api.bean;

import java.util.Map;

public class Annotation {
    private String name;
    private Map<String, Object> methodValues;

    public Annotation(String name, Map<String, Object> methodValues) {
        this.name = name;
        this.methodValues = methodValues;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getMethodValues() {
        return methodValues;
    }

    public void setMethodValues(Map<String, Object> methodValues) {
        this.methodValues = methodValues;
    }

    @Override
    public String toString() {
        return "Annotation{" +
                "name='" + name + '\'' +
                '}';
    }
}
