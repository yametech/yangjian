package cn.ecpark.tool.agent.plugin.hikaricp;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.TimeUnit;

public class HikaricpTest {

    public static void main(String[] args) {
        try {
            DataSource ds = initHikariDataSource();
            Connection connection = ds.getConnection();
            //SQL语句已发送给数据库，并编译好为执行作好准备
            PreparedStatement statement = connection.prepareStatement("select * from user where 1=1");

            //对占位符进行初始化
            //statement.setString(1, "266");
            //statement.setInt(2,1001);
            //执行SQL语句xin
            //statement.addBatch();
            //statement.execute();
            statement.executeQuery();

            statement.close();

            //TimeUnit.SECONDS.sleep(8);
            connection.close();
            ((HikariDataSource) ds).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/*
    @org.junit.Test
    public void test() {

    }*/

    private static DataSource initHikariDataSource() throws Exception {
        HikariConfig config = new HikariConfig();
        //Class.forName("com.mysql.cj.jdbc.Driver");
        //config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        //config.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&amp;characterEncoding=UTF-8&amp;autoReconnect=true");
        config.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai");
        config.setUsername("root");
        config.setPassword("123456");

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "256");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setMaximumPoolSize(1);

        // HikariDataSource 也可以配置
        DataSource ds = new HikariDataSource(config);
        return ds;
    }
}
