/**
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

package com.yametech.yangjian.agent.plugin.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

public class JdbcTest {

    @org.junit.Test
    public void test() {
        try {
            // 1.通过DriverManger注册驱动
            //1.加载驱动程序
            Class.forName("com.mysql.cj.jdbc.Driver");
            //Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai", "root", "123456");
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
            //statement.addBatch("update user set id=1 where name = '1'");
            int c = statement.executeUpdate(sql);
            //statement.executeQuery();
            System.out.println("--------->" + c);
            /*//5，将游标后移一位
            resultSet.next();
            //6，获取数据
            int id = resultSet.getInt(1);
            System.out.println("===================>" + id);*/
            //7，释放资源
            statement.close();
            connection.close();
            TimeUnit.SECONDS.sleep(5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
