package cn.ecpark.tool.javaagent.inter;

@Deprecated
public class IterClass extends AbsIter {
	
	@Override
	void println() {
		System.err.println("22222222");
	}

	@Deprecated
	@Override
	public void print() {
		System.err.print("11111111111");
	}
}
