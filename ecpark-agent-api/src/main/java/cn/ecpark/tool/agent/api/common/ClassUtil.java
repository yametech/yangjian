package cn.ecpark.tool.agent.api.common;

/**
 * @author dengliming
 * @date 2020/1/6
 */
public class ClassUtil {

    private static final String CGLIB_CLASS_SEPARATOR = "$$";

    /**
     * 获取应用原始类名（由于使用CGLIB生成的类为被代理类的一个子类）
     * <p>
     * 如：AccountInfoServiceImpl$$EnhancerBySpringCGLIB$$88abb036 return AccountInfoServiceImpl
     *
     * @param clazz
     * @return
     */
    public static Class<?> getOriginalClass(Class<?> clazz) {
        if (clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null && superclass != Object.class) {
                return superclass;
            }
        }

        return clazz;
    }
}
