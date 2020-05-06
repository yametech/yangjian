package com.yametech.yangjian.agent.plugin.client;

import com.yametech.yangjian.agent.client.IStatusCollect;
import com.yametech.yangjian.agent.client.StatusReturn;

public class TestCustomMetric {
	
	@org.junit.Test
	public void test() throws Exception {
		new MyCustom();
		Thread.sleep(15000);
	}
	
	class MyCustom implements IStatusCollect {

		@Override
		public StatusReturn collect() {
			return StatusReturn.info("test");
		}

		@Override
		public String type() {
			return "test-type";
		}
		
		@Override
		public int interval() {
			return 3;
		}
		
	}
	
}
