package com.yametech.yangjian.agent.core.trace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import brave.Span;
import brave.Tracer;
import brave.Tracer.SpanInScope;
import brave.Tracing;
import brave.propagation.StrictScopeDecorator;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import zipkin2.Endpoint;
import zipkin2.Span.Kind;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

public class ZipkinTest {
	@Rule
	public MockWebServer server = new MockWebServer();

	BlockingQueue<zipkin2.Span> spans = new LinkedBlockingQueue<>();

	/**
	 * Use different tracers for client and server as usually they are on different
	 * hosts.
	 */
	OkHttpSender sender = OkHttpSender.newBuilder().endpoint("http://localhost:9411/api/v2/spans").compressionEnabled(false).build();
	
	Tracing clientTracing = Tracing.newBuilder().localServiceName("client").currentTraceContext(
			ThreadLocalCurrentTraceContext.newBuilder().addScopeDecorator(StrictScopeDecorator.create()).build())
			.spanReporter(spans::add)
//			.spanReporter(AsyncReporter.builder(sender).build())
			.build();
	Tracing serverTracing = Tracing.newBuilder().localServiceName("server").currentTraceContext(
			ThreadLocalCurrentTraceContext.newBuilder().addScopeDecorator(StrictScopeDecorator.create()).build())
			.spanReporter(spans::add)
//			.spanReporter(AsyncReporter.builder(sender).build())
			.build();

	CountDownLatch flushedIncomingRequest = new CountDownLatch(1);

	@Test
	public void createZipkinSpan() throws InterruptedException {
		// 生成Span
		zipkin2.Span span = zipkin2.Span.newBuilder()
				.traceId(0, 1L)
				.id(2L)
				.parentId(3L)
				.name("span-name")
				.localEndpoint(Endpoint.newBuilder().ip("ip").port(8080).serviceName("service-name").build())
				.remoteEndpoint(Endpoint.newBuilder().ip("ip-remote").port(8080).serviceName("service-name-remote").build())
				.kind(Kind.CLIENT)
				.timestamp(System.currentTimeMillis())
				.putTag("tag-key-1", "tag-value-1")
				.putTag("tag-key-2", "tag-value-2")
				.build();
		System.err.println(">>>>" + span);
//		byte[] bytes = SpanBytesEncoder.JSON_V2.encode(span);
	}
	
	@Test
	public void createBraveSpan() throws InterruptedException {
		Tracing tracing = Tracing.newBuilder().localServiceName("service-name")
//				.sampler(RateLimitingSampler.create(10))
				.spanReporter(AsyncReporter.builder(sender).build())
				.build();
		Tracer tracer = tracing.tracer();
		Span root = tracer.nextSpan().name("root-span-name").start();// 创建跨度 root
        try(SpanInScope scope = tracer.withSpanInScope(root)) {// 设置 root 跨度的作用域开始新的跨度 span-name
            // 此处使用 currentTraceContext().get() 获取到当前作用域中的 TraceContext TraceContext 中包含着链路中的关键信息，如 TraceId, parentId, spanId 等
            Span span = tracer.newChild(tracing.currentTraceContext().get())
            			.name("span-name")
            			.annotate("annotate-XXXXX")
            			.kind(brave.Span.Kind.CLIENT)
            			.remoteServiceName("remote-service-name")
//            			.remoteIpAndPort("192.168.0.11", 8080)
            			.tag("tag-key", "tag-value3")
//            			.error(new RuntimeException("测试异常"))
            			.start();
            if(span.remoteIpAndPort("192.168.0.1", 8080)) {
            	System.err.println("成功设置remoteIpAndPort");
            }
            Thread.sleep(15);
            System.out.println("被跟踪的业务代码...");
            span.finish();//结束跨度 span
            System.err.println(root);
            Thread.sleep(100);
        } catch (Exception e) {
            root.error(e);//报错处理
        }
        root.finish();//结束跨度 root
        tracing.close();
        System.err.println(root);
        Thread.sleep(5000);
	}
	
	@Before
	public void setup() {
		server.setDispatcher(new Dispatcher() {
			@Override
			public MockResponse dispatch(RecordedRequest recordedRequest) {
				// pull the context out of the incoming request
				TraceContextOrSamplingFlags extracted = serverTracing.propagation()
						.extractor(RecordedRequest::getHeader).extract(recordedRequest);

				Span span = extracted.context() != null ? serverTracing.tracer().joinSpan(extracted.context())
						: serverTracing.tracer().nextSpan(extracted);
				// start the server side and flush instead of processing a response
				span.name(recordedRequest.getMethod()).kind(Span.Kind.SERVER).start().flush(); 
				flushedIncomingRequest.countDown();
				// eventhough the client doesn't read the response, we return one
				return new MockResponse();
			}
		});
	}

	@After
	public void close() {
		clientTracing.close();
		serverTracing.close();
	}

	@Test(timeout = 5000L)
	public void startWithOneTracerAndStopWithAnother() throws Exception {
		clientTracing.currentTraceContext().get();
		
		// start a new span representing a request
		Span span = clientTracing.tracer().newTrace();

		// inject the trace context into the request
		Request.Builder request = new Request.Builder().url(server.url("/"));
		clientTracing.propagation().injector(Request.Builder::addHeader).inject(span.context(), request);

		// fire off the request asynchronously, totally dropping any response
		new OkHttpClient().newCall(request.build()).enqueue(mock(Callback.class));
		// start the client side and flush instead of processing a response
		span.kind(Span.Kind.CLIENT).start().flush();

		// check that the client send arrived first
		zipkin2.Span clientSpan = spans.take();
		System.err.println("clientSpan=======" + clientSpan);
		assertThat(clientSpan.name()).isNull();
		assertThat(clientSpan.localServiceName()).isEqualTo("client");
		assertThat(clientSpan.kind()).isEqualTo(zipkin2.Span.Kind.CLIENT);

		// check that the server receive arrived last
		zipkin2.Span serverSpan = spans.take();
		System.err.println("serverSpan=======" + serverSpan);
		assertThat(serverSpan.name()).isEqualTo("get");
		assertThat(serverSpan.localServiceName()).isEqualTo("server");
		assertThat(serverSpan.kind()).isEqualTo(zipkin2.Span.Kind.SERVER);

		// check that the server span is shared
		assertThat(serverSpan.shared()).isTrue();

		// check that no spans reported duration
		assertThat(clientSpan.duration()).isNull();
		assertThat(serverSpan.duration()).isNull();
	}

}
