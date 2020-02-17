package cn.ecpark.tool.agent.plugin.okhttp.context;

import cn.ecpark.tool.agent.api.base.IContext;
import cn.ecpark.tool.agent.api.interceptor.IConstructorListener;
import okhttp3.Request;

/**
 * 拦截构造方法为了获取http请求url
 *
 * @author dengliming
 * @date 2019/11/22
 */
public class OkHttpClientInterceptor implements IConstructorListener {

    @Override
    public void constructor(Object thisObj, Object[] allArguments) {
        Request originalRequest = (Request) allArguments[1];
        ((IContext) thisObj)._setAgentContext(ContextConstants.HTTP_REQUEST_URL_CONTEXT_KEY, originalRequest.url().toString());
    }
}
