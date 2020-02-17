package cn.ecpark.tool.agent.plugin.dubbo;

import cn.ecpark.service.user.core.api.AccountInfoService;
import org.junit.After;
import org.junit.Before;

import cn.ecpark.tool.agent.plugin.dubbo.base.RpcClient;
import cn.ecpark.tool.agent.plugin.dubbo.base.RpcServer;

public class DubboTest {
//	private RpcServer rpcServer;
//	private RpcClient client;
	
	@org.junit.Test
	public void server() throws InterruptedException {
		RpcServer rpcServer = initDubboServer();
		Thread.sleep(1000000);
		rpcServer.stop();
	}
	
	@org.junit.Test
	public void client() {
		initDubboClient();
		IDubboService service = RpcClient.getService(IDubboService.class);
		System.err.println(service.hello("aaa"));
	}

	@org.junit.Test
	public void client1() {
		initDubboClient();
		AccountInfoService service = RpcClient.getService(AccountInfoService.class);
		System.err.println(service.countUsersByAppId(123));
	}
	
	@Before
	public void before() {
		
	}
	
	@After
	public void after() {
	}
	
	public static RpcServer initDubboServer() {
		return initDubboServer("1.0.0");
	}
	public static RpcServer initDubboServer(String version) {
		return RpcServer.instance()
				.appName("javaagent-test")
				.addProtocol(20901, 3)
				.addRegistry("zookeeper://10.1.0.64:2181")
				.version(version)
				.start(IDubboService.class, new DubboService());
//		return rpcServer;
	}
	
	public static void initDubboClient() {
		initDubboClient("1.0.0");
	}
	public static void initDubboClient(String version) {
		RpcClient.instance()
				.appName("javaagent-test")
				.addRegistry("zookeeper://10.1.0.64:2181")
				.version(version)
				.timeout(10000);
	}
}
