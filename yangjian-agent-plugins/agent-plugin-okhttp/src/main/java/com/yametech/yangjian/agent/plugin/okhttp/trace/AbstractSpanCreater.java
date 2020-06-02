package com.yametech.yangjian.agent.plugin.okhttp.trace;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.TraceContext;
import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.BeforeResult;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.MicrosClock;
import com.yametech.yangjian.agent.api.trace.ISpanCreater;
import com.yametech.yangjian.agent.api.trace.ISpanSample;
import com.yametech.yangjian.agent.api.trace.SpanInfo;
import com.yametech.yangjian.agent.plugin.okhttp.context.ContextConstants;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Request;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author dengliming
 * @date 2020/4/24
 */
public abstract class AbstractSpanCreater implements ISpanCreater<SpanInfo> {
    protected static final MicrosClock MICROS_CLOCK = new MicrosClock();
    protected Tracer tracer;
    private ISpanSample spanSample;
    private TraceContext.Injector<Headers.Builder> injector;

    @Override
    public void init(Tracing tracing, ISpanSample spanSample) {
        this.tracer = tracing.tracer();
        this.spanSample = spanSample;
        this.injector = tracing.propagation().injector(Headers.Builder::add);
    }

    @Override
    public BeforeResult<SpanInfo> before(Object thisObj, Object[] allArguments, Method method) throws Throwable {
        Request request = (Request) ((IContext) thisObj)._getAgentContext(ContextConstants.HTTP_REQUEST_CONTEXT_KEY);
        if (request == null) {
            return null;
        }
        if (!spanSample.sample()) {
            return null;
        }
        long startTime = MICROS_CLOCK.nowMicros();
        if (startTime == -1L) {
            return null;
        }
        HttpUrl requestUrl = request.url();
        Span span = tracer.nextSpan()
                .kind(Span.Kind.CLIENT)
                .name(requestUrl.toString())
                .tag(Constants.Tags.COMPONENT, Constants.Component.OKHTTP)
                .tag(Constants.Tags.HTTP_METHOD, request.method())
                .tag(Constants.Tags.PEER, requestUrl.host() + ":" + requestUrl.port())
                .start(startTime);
        // 自定义字段为了后续服务拓扑图生成
        ExtraFieldPropagation.set(span.context(), Constants.ExtraHeaderKey.REFERER_SERVICE, Constants.serviceName());
        span.remoteIpAndPort(requestUrl.host(), requestUrl.port());
        Field headersField = Request.class.getDeclaredField("headers");
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(headersField, headersField.getModifiers() & ~Modifier.FINAL);
        headersField.setAccessible(true);
        Headers.Builder headerBuilder = request.headers().newBuilder();
        injector.inject(span.context(), headerBuilder);
        headersField.set(request, headerBuilder.build());
        return new BeforeResult<>(null, new SpanInfo(span, tracer.withSpanInScope(span)), null);
    }
}
