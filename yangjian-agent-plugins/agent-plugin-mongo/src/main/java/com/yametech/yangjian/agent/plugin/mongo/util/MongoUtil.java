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
package com.yametech.yangjian.agent.plugin.mongo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.MongoNamespace;
import com.mongodb.bulk.DeleteRequest;
import com.mongodb.bulk.InsertRequest;
import com.mongodb.bulk.UpdateRequest;
import com.mongodb.bulk.WriteRequest;
import com.mongodb.operation.*;

import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.base.IReportData;
import com.yametech.yangjian.agent.api.bean.MetricData;
import com.yametech.yangjian.agent.api.bean.TimeEvent;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.api.common.LRUCache;
import com.yametech.yangjian.agent.api.common.MultiReportFactory;
import com.yametech.yangjian.agent.api.common.StringUtil;
import com.yametech.yangjian.agent.plugin.mongo.context.ContextConstants;
import org.bson.BsonDocument;

/**
 * @author dengliming
 * @date 2019/12/17
 */
public class MongoUtil {

    private static final int FILTER_LENGTH_LIMIT = 50;

    public static List<TimeEvent> buildRTEvent(long startTime, Object obj) {
        if (obj == null) {
            return null;
        }
        String collection = null;
        if (obj instanceof IContext) {
            MongoNamespace namespace = (MongoNamespace) ((IContext) obj)._getAgentContext(ContextConstants.MONGO_OPERATOR_COLLECTION);
            if (namespace != null) {
                collection = namespace.getCollectionName();
            }
        }

        // 解析集合操作
        List<String> operations = new ArrayList<>();
        if (obj instanceof FindOperation) {
            FindOperation findOperation = (FindOperation) obj;
            collection = findOperation.getNamespace().getCollectionName();
            operations.add(Constants.DbOperation.SELECT);
        } else if ((obj instanceof AggregateOperation || obj instanceof CountOperation) && collection != null) {
            operations.add(Constants.DbOperation.SELECT);
        } else if (obj instanceof MixedBulkWriteOperation) {
            MixedBulkWriteOperation mixedBulkWriteOperation = (MixedBulkWriteOperation) obj;
            collection = mixedBulkWriteOperation.getNamespace().getCollectionName();
            List<? extends WriteRequest> writeRequestList = mixedBulkWriteOperation.getWriteRequests();
            for (WriteRequest request : writeRequestList) {
                if (request instanceof InsertRequest) {
                    operations.add(Constants.DbOperation.INSERT);
                } else if (request instanceof DeleteRequest) {
                    operations.add(Constants.DbOperation.DELETE);
                } else if (request instanceof UpdateRequest) {
                    operations.add(Constants.DbOperation.UPDATE);
                }
            }
        } else if (obj instanceof FindAndUpdateOperation) {
            FindAndUpdateOperation findAndUpdateOperation = (FindAndUpdateOperation) obj;
            collection = findAndUpdateOperation.getNamespace().getCollectionName();
            operations.add(Constants.DbOperation.UPDATE);
        } else if (obj instanceof FindAndDeleteOperation) {
            FindAndDeleteOperation findAndDeleteOperation = (FindAndDeleteOperation) obj;
            collection = findAndDeleteOperation.getNamespace().getCollectionName();
            operations.add(Constants.DbOperation.UPDATE);
        } else if (obj instanceof FindAndReplaceOperation) {
            FindAndReplaceOperation findAndReplaceOperation = (FindAndReplaceOperation) obj;
            collection = findAndReplaceOperation.getNamespace().getCollectionName();
            operations.add(Constants.DbOperation.UPDATE);
        }

        // 封装RT事件统计
        long currentTime = System.currentTimeMillis();
        long useTime = currentTime - startTime;
        Map<String, TimeEvent> timeEventMap = new HashMap<>();
        for (String operation : operations) {
            TimeEvent timeEvent = timeEventMap.get(operation);
            if (timeEvent == null) {
                timeEvent = new TimeEvent();
                timeEvent.setType(Constants.EventType.MONGO);
                timeEvent.setIdentify(collection + Constants.IDENTIFY_SEPARATOR  + operation);
                timeEvent.setNumber(0);
                timeEvent.setEventTime(currentTime);
                timeEvent.setUseTime(useTime);
                timeEventMap.put(operation, timeEvent);
            }
            timeEvent.setNumber(timeEvent.getNumber() + 1);
        }
        return new ArrayList<>(timeEventMap.values());
    }

    public static String getTraceParam(Object obj) {
        if (obj instanceof CountOperation) {
            BsonDocument filter = ((CountOperation) obj).getFilter();
            return StringUtil.shorten(filter.toString(), FILTER_LENGTH_LIMIT);
        } else if (obj instanceof DistinctOperation) {
            BsonDocument filter = ((DistinctOperation) obj).getFilter();
            return StringUtil.shorten(filter.toString(), FILTER_LENGTH_LIMIT);
        } else if (obj instanceof FindOperation) {
            BsonDocument filter = ((FindOperation) obj).getFilter();
            return StringUtil.shorten(filter.toString(), FILTER_LENGTH_LIMIT);
        } else if (obj instanceof GroupOperation) {
            BsonDocument filter = ((GroupOperation) obj).getFilter();
            return StringUtil.shorten(filter.toString(), FILTER_LENGTH_LIMIT);
        } else if (obj instanceof ListCollectionsOperation) {
            BsonDocument filter = ((ListCollectionsOperation) obj).getFilter();
            return StringUtil.shorten(filter.toString(), FILTER_LENGTH_LIMIT);
        } else if (obj instanceof MapReduceWithInlineResultsOperation) {
            BsonDocument filter = ((MapReduceWithInlineResultsOperation) obj).getFilter();
            return StringUtil.shorten(filter.toString(), FILTER_LENGTH_LIMIT);
        } else if (obj instanceof DeleteOperation) {
            List<DeleteRequest> writeRequestList = ((DeleteOperation) obj).getDeleteRequests();
            return getFilter(writeRequestList);
        } else if (obj instanceof InsertOperation) {
            List<InsertRequest> writeRequestList = ((InsertOperation) obj).getInsertRequests();
            return getFilter(writeRequestList);
        } else if (obj instanceof UpdateOperation) {
            List<UpdateRequest> writeRequestList = ((UpdateOperation) obj).getUpdateRequests();
            return getFilter(writeRequestList);
        } else if (obj instanceof CreateCollectionOperation) {
            String filter = ((CreateCollectionOperation) obj).getCollectionName();
            return StringUtil.shorten(filter, FILTER_LENGTH_LIMIT);
        } else if (obj instanceof CreateIndexesOperation) {
            List<String> filter = ((CreateIndexesOperation) obj).getIndexNames();
            return StringUtil.shorten(filter.toString(), FILTER_LENGTH_LIMIT);
        } else if (obj instanceof CreateViewOperation) {
            String filter = ((CreateViewOperation) obj).getViewName();
            return StringUtil.shorten(filter, FILTER_LENGTH_LIMIT);
        } else if (obj instanceof FindAndDeleteOperation) {
            BsonDocument filter = ((FindAndDeleteOperation) obj).getFilter();
            return StringUtil.shorten(filter.toString(), FILTER_LENGTH_LIMIT);
        } else if (obj instanceof FindAndReplaceOperation) {
            BsonDocument filter = ((FindAndReplaceOperation) obj).getFilter();
            return StringUtil.shorten(filter.toString(), FILTER_LENGTH_LIMIT);
        } else if (obj instanceof FindAndUpdateOperation) {
            BsonDocument filter = ((FindAndUpdateOperation) obj).getFilter();
            return StringUtil.shorten(filter.toString(), FILTER_LENGTH_LIMIT);
        } else if (obj instanceof MapReduceToCollectionOperation) {
            BsonDocument filter = ((MapReduceToCollectionOperation) obj).getFilter();
            return StringUtil.shorten(filter.toString(), FILTER_LENGTH_LIMIT);
        } else if (obj instanceof MixedBulkWriteOperation) {
            List<? extends WriteRequest> writeRequestList = ((MixedBulkWriteOperation) obj).getWriteRequests();
            return getFilter(writeRequestList);
        }
        return "";
    }

    private static String getFilter(List<? extends WriteRequest> writeRequestList) {
        StringBuilder params = new StringBuilder();
        for (WriteRequest request : writeRequestList) {
            if (request instanceof InsertRequest) {
                params.append(((InsertRequest) request).getDocument().toString()).append(",");
            } else if (request instanceof DeleteRequest) {
                params.append(((DeleteRequest) request).getFilter()).append(",");
            } else if (request instanceof UpdateRequest) {
                params.append(((UpdateRequest) request).getFilter()).append(",");
            }
            if (params.length() > FILTER_LENGTH_LIMIT) {
                //params.append("...");
                break;
            }
        }
        return StringUtil.shorten(params.toString(), FILTER_LENGTH_LIMIT);
    }

    private static IReportData report = MultiReportFactory.getReport("collect");
    private static final LRUCache CONNECT_URL_CACHE = new LRUCache();

    public static void reportDependency(String peer, String database) {
        if (StringUtil.isEmpty(peer) || StringUtil.isEmpty(database)) {
            return;
        }
        String url = peer + "`" + database;
        CONNECT_URL_CACHE.computeIfAbsent(url, key -> {
            Map<String, Object> params = new HashMap<>();
            params.put(Constants.Tags.PEER, peer);
            params.put(Constants.Tags.DATABASE, database);
            report.report(MetricData.get(null, Constants.DEPENDENCY_PATH + Constants.Component.MONGO, params));
            return true;
        });
    }
}
