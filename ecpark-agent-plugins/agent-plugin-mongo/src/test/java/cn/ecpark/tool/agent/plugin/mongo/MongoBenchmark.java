package cn.ecpark.tool.agent.plugin.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

/**
 * @author dengliming
 * @date 2020/2/6
 */
@BenchmarkMode({Mode.All})// Throughput:整体吞吐量（每秒可以调用次数） AverageTime: 每次调用平均耗时
@Warmup(iterations = 1)// 预热（为了结果更加接近真实情况）
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)// iterations:测试轮次 time:每轮进行的时长 timeUnit:时长单位
@Threads(1)// 测试的线程数 一般为cpu*2
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS) // 输出结果的时间粒度
@State(Scope.Benchmark)
public class MongoBenchmark {

    private MongoClient mongoClient;

    /**
     * benchmark之前执行初始化
     */
    @Setup
    public void before() {
        //连接到MongoDB服务 如果是远程连接可以替换“localhost”为服务器所在IP地址
        //ServerAddress()两个参数分别为 服务器地址 和 端口
        //ServerAddress serverAddress = new ServerAddress("10.1.1.232", 40001);
        ServerAddress serverAddress = new ServerAddress("127.0.0.1", 27017);
        List<ServerAddress> addrs = new ArrayList<>();
        addrs.add(serverAddress);

        //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
        MongoCredential credential = MongoCredential.createScramSha1Credential("a_ecpark_c_rw", "admin", "23xnws".toCharArray());
        List<MongoCredential> credentials = new ArrayList<MongoCredential>();
        credentials.add(credential);

        //通过连接认证获取MongoDB连接
        //MongoClient mongoClient = new MongoClient(addrs, credential, MongoClientOptions.builder().build());
        mongoClient = new MongoClient(addrs);
    }

    /**
     * benchmark之后执行
     */
    @TearDown
    public void after() {
        mongoClient.close();
    }

    /*@Benchmark
    public void test() {

    }*/

    @Benchmark
    public UpdateResult test() {
        MongoDatabase mongoDatabase = mongoClient.getDatabase("test");
        //MongoCollection<Document> collection = mongoDatabase.getCollection("app_log");
        MongoCollection<Document> collection = mongoDatabase.getCollection("user");
        return collection.updateOne(eq("id", 1), new Document("$set", new Document("name", "zhangsan")));
    }

    /**
     * 探针测试
     * -javaagent:D:\workspace\g-server\tool-ecpark-agent\ecpark-agent-core\dist\ecpark-agent\lib\ecpark-agent.jar=args -Dskywalking.agent.service_name=test -Dservice.name=test
     *
     * @param args
     * @throws RunnerException
     */
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(MongoBenchmark.class.getSimpleName())
                .build();
        new Runner(options).run();
    }
}