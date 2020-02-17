package cn.ecpark.tool.javaagent.jvm;

import org.junit.Test;

/**
 * @author zcn
 * @date: 2019-10-17
 **/
public class JVMCollectorTest {

    @Test
    public void test(){
        long totalMemory = 16432216;

        double cpu = 1.9d;
        double mem = 12.8d;

        System.out.println(cpu / 100);
        System.out.println( totalMemory * (mem / 100));
    }
}
