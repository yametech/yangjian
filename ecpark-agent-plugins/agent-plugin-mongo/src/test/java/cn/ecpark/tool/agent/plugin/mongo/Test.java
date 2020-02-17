package cn.ecpark.tool.agent.plugin.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import com.mongodb.client.model.InsertOneModel;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

public class Test {

    @org.junit.Test
    public void test() {
        try {
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
            MongoClient mongoClient = new MongoClient(addrs);
            //连接到数据库
            //MongoDatabase mongoDatabase = mongoClient.getDatabase("ecpark_monitor");
            MongoDatabase mongoDatabase = mongoClient.getDatabase("test");
            //MongoCollection<Document> collection = mongoDatabase.getCollection("app_log");
            MongoCollection<Document> collection = mongoDatabase.getCollection("user");
            MongoCollection<Document> collection2 = mongoDatabase.getCollection("address");
            System.out.println("集合 test 选择成功");

            //检索所有文档
            /**
             * 1. 获取迭代器FindIterable<Document>
             * 2. 获取游标MongoCursor<Document>
             * 3. 通过游标遍历检索出的文档集合
             * */
            FindIterable<Document> findIterable = collection.find().limit(10);
            MongoCursor<Document> mongoCursor = findIterable.iterator();
            while (mongoCursor.hasNext()) {
                System.out.println(mongoCursor.next());
            }

            Bson lookup = new Document("$lookup",
                    new Document("from", "address")
                            .append("localField", "id")
                            .append("foreignField", "userId")
                            .append("as", "look_coll"));
            AggregateIterable<Document> it = collection.aggregate(Arrays.asList(lookup));
            for (Document row : it) {
                System.out.println("==============>" + row.toJson());
            }

            // updateOne
            collection.updateOne(eq("id", 1), new Document("$set", new Document("name", "zhangsan")));
            // updateMany
            //collection.updateMany(lt("id", 1), inc("id", 100));

            // deleteOne
            //collection.deleteOne(eq("id", 1));

            // deleteMany
            //collection.deleteMany(eq("id", 1));

            // findOneAndUpdate
            //collection.findOneAndUpdate(eq("id", 1), new Document("$set", new Document("name", "zhangsan")));

            //collection.findOneAndDelete(eq("id", 1));

            Map<String, Object> obj = new HashMap<>();
            obj.put("userId", 3);
            obj.put("name", "wangwu");
            //collection.insertOne(new Document(obj));

            //collection.insertMany(Arrays.asList(new Document(obj)));

            collection.count();

            Document insertDocument = new Document("name", "GZ");
            insertDocument.append("userId", 99);
            InsertOneModel<Document> insertOneModel = new InsertOneModel<Document>(insertDocument);
            //collection.bulkWrite(Arrays.asList(insertOneModel));

            System.out.println("Connect to database successfully");
            TimeUnit.SECONDS.sleep(5);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

}
