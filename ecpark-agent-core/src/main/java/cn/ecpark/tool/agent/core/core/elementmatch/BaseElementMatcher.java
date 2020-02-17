package cn.ecpark.tool.agent.core.core.elementmatch;

import java.lang.reflect.Type;
import java.util.List;

import cn.ecpark.tool.agent.api.base.IConfigMatch;
import cn.ecpark.tool.agent.api.bean.MethodDefined;
import cn.ecpark.tool.agent.core.log.ILogger;
import cn.ecpark.tool.agent.core.log.LoggerFactory;
import cn.ecpark.tool.agent.util.ClassUtil;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

public abstract class BaseElementMatcher<T> extends ElementMatcher.Junction.AbstractBase<T> {
	private static ILogger log = LoggerFactory.getLogger(BaseElementMatcher.class);
    private IConfigMatch match;
    private Class<?> genericType;
    private String matchType;// 日志显示需要

    private BaseElementMatcher() {
        Type type = ClassUtil.getGenericCls(this.getClass());
        if(!(type instanceof Class)) {
        	log.info("{}获取泛型失败", this.getClass());
        } else {
            genericType = (Class<?>) type;
        }
    }

    BaseElementMatcher(IConfigMatch match, String matchType) {
        this();
        this.match = match;
        this.matchType = matchType;
//        if(matches == null || matches.isEmpty()) {
//            return;
//        }
//        matchers = new ArrayList<>();
//        for(String match : matches) {
//            matchers.add(Pattern.compile(match));
//        }
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean matches(Object o) {
        if(match == null) {
            return false;
        }
        if(genericType == null || !(genericType.isAssignableFrom(o.getClass()))) {
            return false;
        }
//        if(o.toString().indexOf("RabbitmqConsumeAdapterAbs") != -1) {
//        	System.err.println(">>>>>");
//        }
        List<MethodDefined> matchNames = name((T) o);
        boolean isMatch = matchNames.stream().anyMatch(match::isMatch);
        if(o instanceof TypeDescription) {
        	TypeDescription type = ((TypeDescription)o);
        	StringBuilder builder = new StringBuilder();
        	for(String s : ElementMatcherConvert.getInterface(type)) {
        		builder.append(s).append(',');
        	}
//        	if(builder.toString().indexOf("IConsume") != -1) {
//        		System.err.println(">>>>>>");
//        	}
        	log.debug("{} -> {}	{}	{}", isMatch, matchType, o.toString(), builder.toString());
        } else {
        	log.debug("{} -> {}	{}", isMatch, matchType, o.toString());
        }
        return isMatch;
    }

    /**
     * 返回所有需要匹配的方法定义，针对类匹配，返回类所有的方法定义(只要有一个匹配，就返回true)，针对方法匹配，返回当前方法的定义
     * @param t
     * @return
     */
    public abstract List<MethodDefined> name(T t);
    
}
