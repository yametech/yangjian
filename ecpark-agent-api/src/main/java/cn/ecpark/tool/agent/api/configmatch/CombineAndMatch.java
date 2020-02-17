package cn.ecpark.tool.agent.api.configmatch;

import java.util.List;

import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.bean.MethodDefined;

/**
 * 组合And匹配，全部匹配
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年11月7日 下午6:13:10
 */
public class CombineAndMatch implements IConfigMatch {
	private List<IConfigMatch> matches;
	
	public CombineAndMatch(List<IConfigMatch> matches) {
		this.matches = matches;
	}
	
	@Override
	public boolean isMatch(MethodDefined methodDefined) {
		boolean notMatch = matches.stream().anyMatch(match -> !match.isMatch(methodDefined));
		return !notMatch;
	}
	
	@Override
	public String toString() {
		StringBuilder build = new StringBuilder(" AND(");
		matches.forEach(match -> build.append(match.toString()).append('\t'));
		build.append(')');
		return build.toString();
	}
	
}
