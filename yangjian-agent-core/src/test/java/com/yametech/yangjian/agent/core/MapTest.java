package com.yametech.yangjian.agent.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class MapTest {
	
	@org.junit.Test
	public void test3() {
		String str = "SELECT DATE_FORMAT(sta.data_time, '%Y年%m月%d日') AS date\r\n" + 
				"	, DATE_FORMAT(sta.data_time, '%Y-%m-%d') AS dateEn, sta.id\r\n" + 
				"	, sta.org_id AS orgId, sta.org_name AS OrgName, sta.plate_number AS plateNumber\r\n" + 
				"	, ifnull(round(sum(sta.total_mileage) / ?, ?), ?) AS ";
		System.err.println(str.length());
	}
	
	
	/**
	 * 测试ConcurrentHashMap.computeIfAbsent操作key时的原子性
	 * 结果：会阻塞直到computeIfAbsent结束
	 * @throws InterruptedException
	 */
	@org.junit.Test
	public void test() throws InterruptedException {
		String key = "key";
		ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
		Thread t = new Thread(() -> {
			map.computeIfAbsent(key, k-> {
				print("enter key gener");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				print("return key");
				return "value";
			});
		});
		t.start();
		Thread.sleep(1000);
//		print("update key");
//		map.put(key, "value2");
//		print("update key done");
		
		print("get key");
		map.put(key, "value2");
		print("get key done");
	}
	
	/**
	 * 测试遍历Map的过程中其他线程修改了Map中的值，是否会影响遍历
	 * 结果：HashMap如果在遍历过程中修改会导致遍历异常：java.util.ConcurrentModificationException
	 * 		ConcurrentHashMap不会出现异常
	 * @throws InterruptedException
	 */
	@org.junit.Test
	public void test2() throws InterruptedException {
//		Map<String, String> map = new HashMap<>();
		Map<String, String> map = new ConcurrentHashMap<>();
		Thread t = new Thread(() -> {
			for(int i = 0; i < 10; i++) {
				print("put key" + i + "=value" + i);
				map.put("key" + i, "value" + i); 
				try {
					Thread.sleep(800);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
		Thread.sleep(1030);
		for(Entry<String, String> entry : map.entrySet()) {
			print("read " + entry.getKey() + " = " + entry.getValue());
			Thread.sleep(3000);
		}
	}
	
	
	private static void print(String msg) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		System.err.println(sdf.format(new Date()) + " [" + Thread.currentThread().getName() + "]: " + msg);
	}
}
