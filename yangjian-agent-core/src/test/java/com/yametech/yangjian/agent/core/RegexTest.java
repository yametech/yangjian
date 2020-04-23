package com.yametech.yangjian.agent.core;

import java.util.regex.Pattern;

import org.junit.Test;

public class RegexTest {
	
	@Test
	public void test() throws NoSuchMethodException, SecurityException {
		String configKey = "eventSubscribe.group.";
		String keyRegex = configKey.replaceAll("\\.", "\\\\.");
		System.err.println(keyRegex);
		System.err.println(Pattern.matches(keyRegex, configKey));
		System.err.println(Pattern.matches(keyRegex + ".*", configKey + "abcd"));
		
		System.err.println(">>>>>>>>>>>>>>>>>>>>>>>>.");
		
		String name1 = RegexTest.class.getName().replaceAll("\\.", "\\\\.");
		System.err.println(name1);
		String methName = RegexTest.class.getMethod("test").toString();
		System.err.println(methName);
		System.err.println(Pattern.compile(".*" + name1 + "\\..*").matcher(methName).matches());
		
		
		System.err.println(RegexTest.class.getTypeName());
		System.err.println(RegexTest.class.getName());
		System.err.println(new int[1].getClass().getTypeName());
		System.err.println(new int[1].getClass().getName());
		System.err.println(Throwable.class.getTypeName());
		System.err.println(Throwable.class.getName());
		System.err.println(int.class.getTypeName());
		System.err.println(int.class.getName());
	}
}
