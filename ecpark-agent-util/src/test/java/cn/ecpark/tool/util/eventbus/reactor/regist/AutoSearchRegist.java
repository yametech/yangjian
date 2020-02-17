package cn.ecpark.tool.util.eventbus.reactor.regist;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ecpark.tool.agent.util.ClassUtil;
import cn.ecpark.tool.util.eventbus.reactor.consume.BaseConsume;

/**
 * 使用该注册器需保证BaseConsume的实例都有无参构造方法，否则不能自动创建实例
 */
public class AutoSearchRegist implements IRegist {
    private static Logger log = LoggerFactory.getLogger(AutoSearchRegist.class);
    private Class<?> parentCls = BaseConsume.class;
    private String packagePath;
    private Class<?> genericCls;// 类或者父类相同

    public AutoSearchRegist(String packagePath) {
        this.packagePath = packagePath;
    }

    public void setParentCls(Class<?> parentCls) {
        this.parentCls = parentCls;
    }

    public void setGenericCls(Class<?> genericCls) {
        this.genericCls = genericCls;
    }

    @Override
    public List<BaseConsume<Object>> regist() {
        List<BaseConsume<Object>> consumes = new ArrayList<>();
        Set<Class<?>> consumeCls =  ClassUtil.scanPackageBySuper(packagePath, false, parentCls);
        for(Class<?> cls : consumeCls) {
            if(genericCls != null) {
            	Type type = ClassUtil.getGenericCls(cls);
                if(type instanceof Class && !genericCls.isAssignableFrom((Class<?>)type)) {// 泛型类不匹配则跳过
                    continue;
                }
            }
            try {
                consumes.add((BaseConsume<Object>)cls.newInstance());
                log.info("加载消费类{}", cls.getName());
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("自动加载BaseConsume异常", e);
                throw new RuntimeException(e);
            }
        }
        return consumes;
    }
}
