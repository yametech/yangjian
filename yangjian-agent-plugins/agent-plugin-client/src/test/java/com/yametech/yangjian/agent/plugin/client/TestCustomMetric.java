package com.yametech.yangjian.agent.plugin.client;

import com.yametech.yangjian.agent.client.IStatusCollect;
import com.yametech.yangjian.agent.client.StatusReturn;

public class TestCustomMetric {
	
	@org.junit.Test
	public void test() throws Exception {
		new MyCustom();
		new MyCustom2();
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

	class MyCustom2 implements IStatusCollect {

		@Override
		public StatusReturn collect() {
			return StatusReturn.info("test-2");
		}

		@Override
		public String type() {
			return "test-type-2";
		}

		@Override
		public int interval() {
			return 3;
		}

	}
	
}
