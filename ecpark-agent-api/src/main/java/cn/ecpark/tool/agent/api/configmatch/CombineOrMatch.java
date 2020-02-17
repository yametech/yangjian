package cn.ecpark.tool.agent.api.configmatch;

import java.util.List;

import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.bean.MethodDefined;

/**
 * 组合Or匹配，一个匹配则匹配
 * @Description 
 * 
 * @author liuzhao
 * @date 2019年11月7日 下午6:13:10
 */
public class CombineOrMatch implements IConfigMatch {
	private List<IConfigMatch> matches;
	
	public CombineOrMatch(List<IConfigMatch> matches) {
		this.matches = matches;
	}
	
	@Override
	public boolean isMatch(MethodDefined methodDefined) {
		return matches.stream().anyMatch(match -> match.isMatch(methodDefined));
	}
	
	@Override
	public String toString() {
		StringBuilder build = new StringBuilder(" OR(");
		matches.forEach(match -> build.append(match.toString()).append('\t'));
		build.append(')');
		return build.toString();
	}
	
}
