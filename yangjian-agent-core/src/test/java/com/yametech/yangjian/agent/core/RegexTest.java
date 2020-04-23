package com.yametech.yangjian.agent.core;

import java.util.regex.Pattern;

import org.junit.Test;

import com.yametech.yangjian.agent.core.trace.TraceTest;

public class RegexTest {
	
	@Test
	public void test() throws NoSuchMethodException, SecurityException {
		String configKey = "eventSubscribe.group.";
		String keyRegex = configKey.replaceAll("\\.", "\\\\.");
		System.err.println(keyRegex);
		System.err.println(Pattern.matches(keyRegex, configKey));
		System.err.println(Pattern.matches(keyRegex + ".*", configKey + "abcd"));
		
		System.err.println(">>>>>>>>>>>>>>>>>>>>>>>>.");
		
		String name1 = TraceTest.class.getName().replaceAll("\\.", "\\\\.");
		System.err.println(name1);
		String methName = TraceTest.class.getMethod("test").toString();
		System.err.println(methName);
		System.err.println(Pattern.compile(".*" + name1 + "\\..*").matcher(methName).matches());
	}
}
