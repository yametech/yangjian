package com.yametech.yangjian.agent.plugin.dubbo.trace;

import brave.Span;
import brave.Tracer.SpanInScope;

public class SpanInfo {
	private Span span;
	private SpanInScope scope;
	
	public SpanInfo(Span span, SpanInScope scope) {
		this.span = span;
		this.scope = scope;
	}
	
	public SpanInScope getScope() {
		return scope;
	}
	
	public Span getSpan() {
		return span;
	}
}
