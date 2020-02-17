package cn.ecpark.tool.agent.api.configmatch;

import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.bean.MethodDefined;

/**
 * 方法指定参数类型匹配
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年11月7日 下午6:12:13
 */
public class MethodArgumentIndexMatch implements IConfigMatch {
	private int index;
	private String type;
	
	public MethodArgumentIndexMatch(int index, String type) {
		this.index = index;
		this.type = type;
	}
	
	@Override
	public boolean isMatch(MethodDefined methodDefined) {
		if(methodDefined.getParams() == null || methodDefined.getParams().length <= index) {
			return false;
		}
		return methodDefined.getParams()[index].equals(type);
	}
	
	@Override
	public String toString() {
		return "argument index " + index + " is " + type;
	}
	
}
