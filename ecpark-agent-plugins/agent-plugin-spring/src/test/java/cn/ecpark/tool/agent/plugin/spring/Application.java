package cn.ecpark.tool.agent.plugin.spring;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * -javaagent:D:\workspace\g-server\tool-ecpark-agent\ecpark-agent-core\dist\ecpark-agent\lib\ecpark-agent.jar=args -Dskywalking.agent.service_name=test -Dservice.name=test
 *
 * @author dengliming
 * @date 2020/2/5
 */
@RestController
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.setProperty("server.port", "8080");
        properties.setProperty("server.tomcat.max-threads", "400");
        SpringApplication application = new SpringApplication(Application.class);
        application.setDefaultProperties(properties);
        application.run(args);
    }

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        //Class.forName("com.mysql.cj.jdbc.Driver");
        //config.setDriverClassName("com.mysql.jdbc.Driver");
        //config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setDriverClassName("com.mysql.jdbc.Driver");
        //config.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&amp;characterEncoding=UTF-8&amp;autoReconnect=true");
        config.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai");
        config.setUsername("root");
        config.setPassword("123456");

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "256");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setMaximumPoolSize(500);
        return new HikariDataSource(config);
    }

    @Bean
    public Jedis jedis() {
        //设置连接池的相关配置
        /*JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(20);
        poolConfig.setMaxIdle(1);
        poolConfig.setMaxWaitMillis(2000);
        poolConfig.setTestOnBorrow(false);
        poolConfig.setTestOnReturn(false);
        return new JedisPool(poolConfig, "127.0.0.1", 6379);*/
        return new Jedis("127.0.0.1", 6379);
    }

    @Autowired
    private DataSource dataSource;
    @Autowired
    private Jedis jedis;

    @RequestMapping("/ping")
    public String ping() {
        return "ok!";
    }

    @RequestMapping("/user/{id}")
    public String testDb(@PathVariable String id) throws SQLException {
        String name = jedis.get(id);
        if (name == null) {
            Connection connection = null;
            PreparedStatement statement = null;
            try {
                connection = dataSource.getConnection();
                //SQL语句已发送给数据库，并编译好为执行作好准备
                statement = connection.prepareStatement("select * from user where id=?");
                //对占位符进行初始化
                statement.setString(1, id);
                //statement.setInt(2,1001);
                //执行SQL语句
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    name = resultSet.getString("name");
                    jedis.set(id, name);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (statement != null) {
                    statement.close();
                }

                if (connection != null) {
                    connection.close();
                }
            }
        }
        return name;
    }
}
