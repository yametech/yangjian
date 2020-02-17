package cn.ecpark.tool.agent.api.bean;

public class BeforeResult<T> {
    // 如果存在该值，则使用该值作为返回值，不再调用拦截方法
    private Object ret;
    // 该值会传递给after方法作为执行参数，即before向after传递的变量
    private T localVar;
    // 全局变量，会将所有的before产生的该值传递给每个after
    private Object globalVar;

    public BeforeResult(Object ret, T localVar, Object globalVar) {
        this.ret = ret;
        this.localVar = localVar;
        this.globalVar = globalVar;
    }

    public Object getRet() {
        return ret;
    }

    public void setRet(Object ret) {
        this.ret = ret;
    }

    public T getLocalVar() {
        return localVar;
    }

    public void setLocalVar(T localVar) {
        this.localVar = localVar;
    }
    
    public Object getGlobalVar() {
		return globalVar;
	}
    
    public void setGlobalVar(Object globalVar) {
		this.globalVar = globalVar;
	}
}
