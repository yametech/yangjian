package cn.ecpark.tool.javaagent;

public class Main {
	
	/**
	 * 测试javaagent
	 * 		-javaagent:E:\eclipse-workspace\tool-ecpark-monitor\ecpark-agent\target\ecpark-agent.jar
	 * 
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
//		java.time.Duration.ofHours(12);
//		BlockingQueue queue = new ArrayBlockingQueue<>(10);
//		queue.add("123");
//		
//		ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 8, 10000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10));
//		executor.execute(() -> {
//			System.err.println("execute");
//		});
		testTime();
		Thread.sleep(3000);
		
//		new IterClass().print();
		
		System.exit(0);
	}
	
	private static void testTime() {
		TestService test = new TestService();
		long start = System.currentTimeMillis();
		for(int i = 0; i < 10; i++) {
//			test.m1();
//			test.m2("test");
			test.add(1, 2);
			TestService.multiply(2, 3);
		}
		System.err.println(System.currentTimeMillis() - start);
//		test.toString();
	}
}
