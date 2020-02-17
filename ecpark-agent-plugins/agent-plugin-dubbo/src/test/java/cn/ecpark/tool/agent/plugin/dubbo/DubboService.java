package cn.ecpark.tool.agent.plugin.dubbo;

import java.util.Random;
import java.util.concurrent.locks.LockSupport;

public class DubboService implements IDubboService {

	@Override
	public String hello(String name) {
//		LockSupport.parkNanos(new Random().nextInt() % 3);
		System.err.println("call hello");
		return "hello " + name;
	}

}
