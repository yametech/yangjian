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

package com.yametech.yangjian.agent.tests.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertOneModel;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.tests.tool.AbstractAgentTest;
import com.yametech.yangjian.agent.tests.tool.bean.EventMetric;
import org.bson.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;
import zipkin2.Span;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MongoDBPluginTest extends AbstractAgentTest {

    private static MongoDBContainer mongoDBContainer;

    @BeforeClass
    public static void setUp() {
        mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));
        mongoDBContainer.start();
    }

    @AfterClass
    public static void tearDown() {
        mongoDBContainer.stop();
    }

    @Test
    public void testInsertAndFind() {
        MongoClient mongoClient = new MongoClient(mongoDBContainer.getHost(), mongoDBContainer.getFirstMappedPort());
        MongoDatabase mongoDatabase = mongoClient.getDatabase("test");
        MongoCollection<Document> collection = mongoDatabase.getCollection("user");

        Document insertDocument = new Document("key", "value");
        insertDocument.append("userId", 99);
        collection.bulkWrite(Arrays.asList(new InsertOneModel<>(insertDocument)));

        FindIterable<Document> findIterable = collection.find().limit(10);
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        mongoCursor.next();

        List<EventMetric> metricList = mockMetricServer.waitForMetrics(2);
        List<Span> spanList = mockTracerServer.waitForSpans(2);

        assertNotNull(metricList);
        assertNotNull(spanList);
        assertEquals(2, spanList.size());
        assertEquals(2, metricList.size());

        System.out.println(metricList.get(0));
        Map<String, String> tags = spanList.get(0).tags();
        assertEquals("test", tags.get(Constants.Tags.DATABASE));
        assertEquals("MongoDB/MixedBulkWriteOperation", spanList.get(0).name());
    }
}
