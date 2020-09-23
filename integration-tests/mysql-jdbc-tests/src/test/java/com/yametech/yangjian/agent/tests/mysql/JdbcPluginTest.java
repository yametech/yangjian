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

package com.yametech.yangjian.agent.tests.mysql;

import com.yametech.yangjian.agent.tests.tool.AbstractAgentTest;
import org.junit.Rule;
import org.testcontainers.containers.MySQLContainer;
import zipkin2.Span;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JdbcPluginTest extends AbstractAgentTest {

    @Rule
    public MySQLContainer mysql = new MySQLContainer()
            .withDatabaseName("yangjian_test")
            .withUsername("ym")
            .withPassword("123456");

    @org.junit.Test
    public void test() {
        update("DROP TABLE IF EXISTS ym_apm");
        update("CREATE TABLE ym_apm (id INT NOT NULL, name VARCHAR(255))");
        update("UPDATE ym_apm SET name=12 WHERE id=?", 1001);

        List<Span> spans = mockTracerServer.waitForSpans(3, Duration.ofSeconds(5).toMillis());
        assertNotNull(spans);
        assertEquals(3, spans.size());
    }

    private void update(String sql, Object... args) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                pstmt.setObject(i + 1, args[i]);
            }
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResource(conn, pstmt, null);
        }
    }

    private Connection getConnection() throws Exception {
        String url = mysql.getJdbcUrl() + "?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai";
        Class.forName("com.mysql.jdbc.Driver");//注册mysql数据库驱动
        return DriverManager.getConnection(url, mysql.getUsername(), mysql.getPassword());
    }

    private void closeResource(Connection conn, Statement stat, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (stat != null) {
            try {
                stat.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
