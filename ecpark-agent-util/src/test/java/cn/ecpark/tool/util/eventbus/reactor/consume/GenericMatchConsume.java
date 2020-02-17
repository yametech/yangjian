package cn.ecpark.tool.util.eventbus.reactor.consume;


import java.lang.reflect.Type;

import cn.ecpark.tool.agent.util.ClassUtil;

public abstract class GenericMatchConsume<T> implements BaseConsume<T> {
    private Class<?> genericCls;// 泛型类型

    public GenericMatchConsume() {
    	Type type = ClassUtil.getGenericCls(this.getClass());
    	if(type instanceof Class) {
    		this.genericCls = (Class<?>) type;
    	}
    }

    @Override
    public boolean test(T o) {
        return o != null && o.getClass().equals(genericCls);
    }

}
