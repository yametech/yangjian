package cn.ecpark.tool.agent.api.configmatch;

import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.bean.MethodDefined;

/**
 * 取非匹配
 *
 * @author dengliming
 * @date 2019/11/22
 */
public class NotMatch implements IConfigMatch {
    private IConfigMatch match;

    public NotMatch(IConfigMatch match) {
        this.match = match;
    }

    @Override
    public boolean isMatch(MethodDefined methodDefined) {
        return match != null && !match.isMatch(methodDefined);
    }

    @Override
    public String toString() {
        StringBuilder build = new StringBuilder(" NOT(");
        build.append(match.toString()).append(')');
        return build.toString();
    }
}
