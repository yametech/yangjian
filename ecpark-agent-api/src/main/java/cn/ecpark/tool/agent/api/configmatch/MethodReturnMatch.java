package cn.ecpark.tool.agent.api.configmatch;

import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.bean.MethodDefined;

/**
 * 方法返回值匹配
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年11月7日 下午6:12:45
 */
public class MethodReturnMatch implements IConfigMatch {
	private String returnCls;
	
	public MethodReturnMatch(String returnCls) {
		this.returnCls = returnCls;
	}
	
	@Override
	public boolean isMatch(MethodDefined methodDefined) {
		return methodDefined.getMethodRet() != null && methodDefined.getMethodRet().equals(returnCls);
	}
	
	@Override
	public String toString() {
		return returnCls;
	}
	
}
