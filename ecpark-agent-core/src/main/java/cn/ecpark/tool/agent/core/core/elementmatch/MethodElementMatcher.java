package cn.ecpark.tool.agent.core.core.elementmatch;

import java.util.Arrays;
import java.util.List;

import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.bean.MethodDefined;
import net.bytebuddy.description.method.MethodDescription;

/**
 * 使用正则匹配类方法
 *
 * 匹配内容示例：
 *  public boolean java.lang.Object.equals(java.lang.Object)
 *  public native int java.lang.Object.hashCode()
 *  protected native java.lang.Object java.lang.Object.clone() throws java.lang.CloneNotSupportedException
 *  public void com.liuzz.myproject.app.AgentInterceptorTimed.helloSleep(java.lang.String) throws java.lang.InterruptedException
 *
 */
public class MethodElementMatcher extends BaseElementMatcher<MethodDescription> {
    public MethodElementMatcher(IConfigMatch match, String matchType) {
        super(match, matchType);
    }

    @Override
    public List<MethodDefined> name(MethodDescription methodDescription) {
        return Arrays.asList(ElementMatcherConvert.convert(methodDescription.asDefined()));
    }
    
}
