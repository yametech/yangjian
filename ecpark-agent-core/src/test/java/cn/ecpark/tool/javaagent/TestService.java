package cn.ecpark.tool.javaagent;

public class TestService {
	
	public void m1() {
		int a = 1 + 10;
		int b = a + 11;
//		System.err.println("call m1");
	}
	
	public String m2(String a1) {
		int a = 1 + 10;
		int b = a + 11;
//		System.err.println("call m2");
		return a1;
	}
	
	public int add(int a, int b) {
		return a + b;
	}
	
	public static int multiply(int a, int b) {
		return a * b;
	}
}
