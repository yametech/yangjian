package com.yametech.yangjian.agent.core.trace.base;

import zipkin2.Span;

public class TraceSpan {
	private Span span;
	
	public Span getSpan() {
		return span;
	}
	
	public void setSpan(Span span) {
		this.span = span;
	}
}
