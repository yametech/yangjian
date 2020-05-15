package com.yametech.yangjian.agent.client.test;

import com.yametech.yangjian.agent.client.annotation.AutoInstance;
import com.yametech.yangjian.agent.client.annotation.IgnoreParams;
import com.yametech.yangjian.agent.client.annotation.Register;
import com.yametech.yangjian.agent.client.annotation.Subscribe;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

@AutoInstance
@Register({List.class, Map.class})
public class SubscriberTest {
    @Test
    @IgnoreParams
    @Subscribe(parent="cn.ecpark.TestClass", methodName="testMethod", argumentNumber=3, argumentType={"", "java.lang.String"})
    @Subscribe(interfaces={"cn.ecpark.TestInterface"}, methodName="testMethod", argumentNumber=2, argumentType={"", "java.lang.String"})
    public void subscribe() throws InvocationTargetException, IllegalAccessException {

    }
}
