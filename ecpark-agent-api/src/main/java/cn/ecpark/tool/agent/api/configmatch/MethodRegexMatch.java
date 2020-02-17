package cn.ecpark.tool.agent.api.configmatch;

import java.util.regex.Pattern;

import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.bean.MethodDefined;

/**
 * 方法定义正则匹配
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年11月7日 下午6:12:45
 */
public class MethodRegexMatch implements IConfigMatch {
	private Pattern pattern;
	
	public MethodRegexMatch(String methodRegex) {
		pattern = Pattern.compile(methodRegex);
	}
	
	@Override
	public boolean isMatch(MethodDefined methodDefined) {
		return methodDefined.getMethodDes() != null && pattern.matcher(methodDefined.getMethodDes()).matches();
	}
	
	@Override
	public String toString() {
		return pattern.pattern();
	}
	
}
