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

package com.yametech.yangjian.agent.plugin.mongo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.bulk.DeleteRequest;
import com.mongodb.bulk.InsertRequest;
import com.mongodb.bulk.UpdateRequest;
import com.mongodb.bulk.WriteRequest;
import com.mongodb.operation.AggregateOperation;
import com.mongodb.operation.CountOperation;
import com.mongodb.operation.FindAndDeleteOperation;
import com.mongodb.operation.FindAndReplaceOperation;
import com.mongodb.operation.FindAndUpdateOperation;
import com.mongodb.operation.FindOperation;
import com.mongodb.operation.MixedBulkWriteOperation;

import com.yametech.yangjian.agent.api.base.IContext;
import com.yametech.yangjian.agent.api.bean.TimeEvent;
import com.yametech.yangjian.agent.api.common.Constants;
import com.yametech.yangjian.agent.plugin.mongo.context.ContextConstants;

/**
 * @author dengliming
 * @date 2019/12/17
 */
public class MongoUtil {

    public static List<TimeEvent> buildRTEvent(long startTime, Object obj) {
        if (obj == null) {
            return null;
        }
        String collection = null;
        if (obj instanceof IContext) {
            collection = (String) ((IContext) obj)._getAgentContext(ContextConstants.MONGO_OPERATOR_COLLECTION);
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
}
