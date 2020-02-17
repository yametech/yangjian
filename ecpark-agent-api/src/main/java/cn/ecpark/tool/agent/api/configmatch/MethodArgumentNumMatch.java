package cn.ecpark.tool.agent.api.configmatch;

import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.bean.MethodDefined;

/**
 * 方法参数个数匹配
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年11月7日 下午6:14:13
 */
public class MethodArgumentNumMatch implements IConfigMatch {
	private int argumentNum;
	
	public MethodArgumentNumMatch(int argumentNum) {
		this.argumentNum = argumentNum;
	}
	
	@Override
	public boolean isMatch(MethodDefined methodDefined) {
		int num = methodDefined.getParams() == null ? 0 : methodDefined.getParams().length;
		return num == argumentNum;
	}
	
	@Override
	public String toString() {
		return "argument num is " + argumentNum;
	}
	
}
