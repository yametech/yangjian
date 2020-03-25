/*
 * Copyright 2020 yametech.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yametech.yangjian.agent.plugin.druid;

import com.alibaba.druid.filter.logging.LogFilter;
import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.pool.DruidDataSource;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Test {

	@org.junit.Test
	public void test(){
		try {
			DruidDataSource druidDataSource = initDruidDataSource();
			Connection connection = druidDataSource.getConnection();
			//Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/ecpark_monitor?useUnicode=true&amp;characterEncoding=UTF-8&amp;autoReconnect=true", "root", "123456");
			//2，通过Connection获取一个操作sql语句的对象Statement
			Statement statement = connection.createStatement();
			//3，拼接sql语句
			String sql = "update user set id=1 where name = '1'";
			//4，查询，返回的结果放入ResultSet对象中。
			//ResultSet resultSet = statement.execute(sql);
			//statement.addBatch(sql);
			//statement.addBatch(sql);
			//SQL语句已发送给数据库，并编译好为执行作好准备
			//PreparedStatement statement = connection.prepareStatement("update user set name=12 where id=?");

			//对占位符进行初始化
			//statement.setString(1, "266");
			//statement.setInt(1,1001);
			//执行SQL语句xin
			//statement.addBatch();
			//statement.executeUpdate(sql);
			//statement.executeUpdate("update user set id=1 where name = '1'");
			//int[] t1 = statement.executeBatch();
			statement.addBatch("update user set id=1 where name = '1'");
			statement.executeBatch();
			//statement.executeQuery();
			//System.out.println("--------->" + t1);
            /*//5，将游标后移一位
            resultSet.next();
            //6，获取数据
            int id = resultSet.getInt(1);//第一行的第一列数据，我们知道是id，也知道是int类型，
            System.out.println("===================>" + id);*/
			TimeUnit.SECONDS.sleep(2);
			//7，释放资源
			statement.close();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DruidDataSource initDruidDataSource() {
		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
		druidDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai");
		druidDataSource.setUsername("root");
		druidDataSource.setPassword("123456");
		druidDataSource.setMinIdle(1);
		druidDataSource.setMaxActive(1);
		LogFilter logFilter = new Slf4jLogFilter();
		logFilter.setStatementExecutableSqlLogEnable(false);
		druidDataSource.setProxyFilters(Arrays.asList(logFilter));
		return druidDataSource;
	}
}
