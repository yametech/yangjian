package cn.ecpark.tool.agent.api.base;

import cn.ecpark.tool.agent.api.bean.MethodDefined;

public interface IConfigMatch {
	
	boolean isMatch(MethodDefined methodDefined);
}
