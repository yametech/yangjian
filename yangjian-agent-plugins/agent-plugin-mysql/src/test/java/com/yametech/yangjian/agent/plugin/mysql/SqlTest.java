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
package com.yametech.yangjian.agent.plugin.mysql;

import java.util.List;
import java.util.Map;

import com.yametech.yangjian.agent.plugin.mysql.commons.druid.sql.SQLUtils;
import com.yametech.yangjian.agent.plugin.mysql.commons.druid.sql.ast.SQLStatement;
import com.yametech.yangjian.agent.plugin.mysql.commons.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.yametech.yangjian.agent.plugin.mysql.commons.druid.sql.visitor.SchemaStatVisitor;
import com.yametech.yangjian.agent.plugin.mysql.commons.druid.stat.TableStat;
import com.yametech.yangjian.agent.plugin.mysql.commons.druid.util.JdbcConstants;

/**
 * @author dengliming
 * @date 2019/11/25
 */
public class SqlTest {

    public static void main(String[] args) throws Exception {
        //t();
        //t1();
        //t2();
        //t2();
        t3();
    }

    public static void t3() {
        //String sql = "SELECT ID, NAME, AGE FROM USER WHERE ID = ?";
        long s = System.currentTimeMillis();
        //String sql = "INSERT INTO db2_name(field3,field4) SELECT field3,field4 FROM db1_name  ";
        //String sql = "INSERT INTO db2_name(field3,field4) SELECT field3,field4 FROM db1_name;\nINSERT INTO db2_name(field3,field4) SELECT field3,field4 FROM db1_name";
        //String sql = "UPDATE Groups LEFT JOIN (SELECT GroupId, MIN(ValWithinGroup) AS baseVal FROM Groups GROUP BY GroupId) AS GrpSum USING (GroupId) SET ValWithinGroup=ValWithinGroup-baseVal;\n";
        /*String sql ="select ship_power.gun_power, ship_info.*\n" +
                "FROM\n" +
                "\t(\n" +
                "\t\tselect s.name as ship_name, sum(g.power) as gun_power, max(callibr) as max_callibr\n" +
                "\t\tfrom\n" +
                "\t\t\tships s inner join ships_guns sg on s.id = sg.ship_id inner join guns g on g.id = sg.guns_id\n" +
                "\t\tgroup by s.name\n" +
                "\t) ship_power\n" +
                "\tinner join\n" +
                "\t(\n" +
                "\t\tselect s.name as ship_name, sc.class_name, sc.tonange, sc.max_length, sc.start_build, sc.max_guns_size\n" +
                "\t\tfrom\n" +
                "\t\t\tships s inner join ship_class sc on s.class_id = sc.id\n" +
                "\t) ship_info using (ship_name) where ship_power=1\n" +
                "order by ship_power.ship_name;";*/
        /*String sql = "SELECT country FROM Websites\n" +
                " UNION\n" +
                " SELECT country FROM apps" +
                "   left join c on c.id=country.id where c.a in(1,2,3)\n" +
                " ORDER BY country;";*/
        String sql = "INSERT INTO \n" +
                "\n" +
                "items(name,city,price,number,picture) \n" +
                "\n" +
                "VALUES\n" +
                "\n" +
                "('耐克运动鞋','广州',500,1000,'003.jpg'),\n" +
                "\n" +
                "('耐克运动鞋2','广州2',500,1000,'002.jpg');";
        sql = "update user set id=1 where name = '1'";
        // 参数化sql
        String parameterizedSql = ParameterizedOutputVisitorUtils.parameterize(sql, JdbcConstants.MYSQL);
        System.out.println("parameterize:" + parameterizedSql);

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLStatement stmt = stmtList.get(0);
        stmt.accept(statVisitor);

        //获取表名称
        //System.out.println("Tables : " + visitor.get());
        //获取操作方法名称,依赖于表名称
        //System.out.println("Manipulation : " + visitor.getTables());
        for (Map.Entry<TableStat.Name, TableStat> entry : statVisitor.getTables().entrySet()) {
            TableStat tableStat = entry.getValue();
            System.out.println(entry.getKey().getName() + "=====>" +
                    tableStat.getUpdateCount() + "/" + tableStat.getSelectCount() + "/" +
                    tableStat.getInsertCount() + "/" + tableStat.getDeleteCount());
        }
        //System.out.println("Sql : " + sql);
        //获取字段名称
        System.out.println("fields : " + statVisitor.getColumns());
        System.out.println("====================>cost:" + (System.currentTimeMillis() - s));
    }
}
