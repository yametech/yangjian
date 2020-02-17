package cn.ecpark.tool.agent.core.core.elementmatch;

import java.util.ArrayList;
import java.util.List;

import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.bean.MethodDefined;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;

/**
 * 使用正则匹配类包（不含方法定义）
 *
 * 匹配内容示例：
 *  com.liuzz.myproject.app.AgentInterceptorTimed
 *
 */
public class ClassElementMatcher extends BaseElementMatcher<TypeDescription> {

    public ClassElementMatcher(IConfigMatch match, String matchType) {
        super(match, matchType);
    }

    @Override
    public List<MethodDefined> name(TypeDescription typeDescription) {
    	List<MethodDefined> matchNames = new ArrayList<>();
    	MethodList<MethodDescription.InDefinedShape> methods = typeDescription.getDeclaredMethods();
        for(MethodDescription.InDefinedShape inDefinedShape : methods) {
            if(!inDefinedShape.isMethod()) {
                continue;
            }
//            System.out.println(convert(inDefinedShape).toString());
            matchNames.add(ElementMatcherConvert.convert(inDefinedShape));
        }
    	return matchNames;
    }
    
   
}
