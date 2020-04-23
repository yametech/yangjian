package com.yametech.yangjian.agent.core.trace;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import brave.Tracer;
import brave.Tracer.SpanInScope;
import brave.Tracing;
import io.opentelemetry.common.AttributeValue;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.correlationcontext.CorrelationContextManagerSdk;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.TracerSdk;
import io.opentelemetry.trace.Span;
import io.opentelemetry.trace.Status;
import zipkin2.Span.Kind;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

public class TraceTest {
	
	@Test
	public void testOpentracing() {
		
	}
	
	@Test
	public void testOpentelemetry() throws InterruptedException {
		TracerSdk tracerSdk = OpenTelemetrySdk.getTracerProvider().get("instrumentationName");
		
		Span span = tracerSdk.spanBuilder("span_name")
				.setNoParent()
			.setSpanKind(Span.Kind.CLIENT)
//			.setStartTimestamp(startTimestamp)// 默认为当前时间
//			.addLink(SpanContext., attributes)
			.startSpan();
		Map<String, AttributeValue> attributes = new HashMap<>();
		attributes.put("attribute", AttributeValue.stringAttributeValue("AttributeValue"));
		attributes.put("attribute1", AttributeValue.stringAttributeValue("AttributeValue1"));
		span.addEvent("event_name", attributes);
		span.setStatus(Status.OK);
		span.setAttribute("setAttribute", "setAttribute-value");
		Thread.sleep(100);
		span.end();
		System.err.println(((ReadableSpan)span).toSpanData());
		
		
		CorrelationContextManagerSdk context = OpenTelemetrySdk.getCorrelationContextManager();
//		context.getCurrentContext().
	}
	
}
