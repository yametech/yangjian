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
package com.yametech.yangjian.agent.plugin.jedis.bean;

import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.configmatch.CombineOrMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodNameMatch;

import java.util.Arrays;

/**
 * @author dengliming
 * @date 2020/5/6
 */
public enum JedisMethodMatcher {
    INSTANCE;

    public IConfigMatch JedisClusterMethodMatch() {
        return new CombineOrMatch(Arrays.asList(
                named("zcount"), named("sunionstore"), named("zunionstore"), named("del"), named("zinterstore"),
                named("echo"), named("hscan"), named("psubscribe"), named("type"), named("sinterstore"), named("setex"),
                named("zlexcount"), named("brpoplpush"), named("bitcount"), named("llen"), named("zscan"), named("lpushx"),
                named("bitpos"), named("setnx"), named("hvals"), named("evalsha"), named("substr"), named("geodist"),
                named("zrangeByLex"), named("geoadd"), named("expire"), named("bitop"), named("zrangeByScore"), named("smove"),
                named("lset"), named("decrBy"), named("pttl"), named("scan"), named("zrank"), named("blpop"), named("rpoplpush"),
                named("zremrangeByLex"), named("get"), named("lpop"), named("persist"), named("scriptExists"), named("georadius"),
                named("set"), named("srandmember"), named("incr"), named("setbit"), named("hexists"), named("expireAt"),
                named("pexpire"), named("zcard"), named("bitfield"), named("zrevrangeByLex"), named("sinter"), named("srem"),
                named("getrange"), named("rename"), named("zrevrank"), named("exists"), named("setrange"), named("zremrangeByRank"),
                named("sadd"), named("sdiff"), named("zrevrange"), named("getbit"), named("scard"), named("sdiffstore"),
                named("zrevrangeByScore"), named("zincrby"), named("rpushx"), named("psetex"), named("zrevrangeWithScores"),
                named("strlen"), named("hdel"), named("zremrangeByScore"), named("geohash"), named("brpop"), named("lrem"),
                named("hlen"), named("decr"), named("scriptLoad"), named("lpush"), named("lindex"), named("zrange"), named("incrBy"),
                named("getSet"), named("ltrim"), named("incrByFloat"), named("rpop"), named("sort"), named("zrevrangeByScoreWithScores"),
                named("pfadd"), named("eval"), named("linsert"), named("pfcount"), named("hkeys"), named("hsetnx"), named("hincrBy"),
                named("hgetAll"), named("hset"), named("spop"), named("zrangeWithScores"), named("hincrByFloat"), named("hmset"),
                named("renamenx"), named("zrem"), named("msetnx"), named("hmget"), named("sunion"), named("hget"), named("zadd"),
                named("move"), named("subscribe"), named("geopos"), named("mset"), named("zrangeByScoreWithScores"), named("zscore"),
                named("pexpireAt"), named("georadiusByMember"), named("ttl"), named("lrange"), named("smembers"), named("pfmerge"),
                named("rpush"), named("publish"), named("mget"), named("sscan"), named("append"), named("sismember")
        ));
    }

    public IConfigMatch JedisMethodMatch() {
        return new CombineOrMatch(Arrays.asList(
                JedisClusterMethodMatch(),
                named("sentinelMasters"), named("clusterReplicate"), named("readonly"), named("randomKey"),
                named("clusterInfo"), named("pubsubNumSub"), named("sentinelSlaves"), named("clusterSetSlotImporting"),
                named("clusterSlaves"), named("clusterFailover"), named("clusterSetSlotMigrating"), named("watch"),
                named("clientKill"), named("clusterKeySlot"), named("clusterCountKeysInSlot"), named("sentinelGetMasterAddrByName"),
                named("objectRefcount"), named("clusterMeet"), named("sentinelSet"), named("clusterSetSlotNode"), named("clusterAddSlots"),
                named("pubsubNumPat"), named("slowlogGet"), named("sentinelReset"), named("clusterNodes"), named("sentinelMonitor"),
                named("configGet"), named("objectIdletime"), named("pubsubChannels"), named("getParams"), named("sentinelRemove"),
                named("migrate"), named("clusterForget"), named("asking"), named("keys"), named("clientSetname"), named("clusterSaveConfig"),
                named("configSet"), named("dump"), named("clusterFlushSlots"), named("clusterGetKeysInSlot"), named("clusterReset"),
                named("restore"), named("clusterDelSlots"), named("sentinelFailover"), named("clusterSetSlotStable"), named("objectEncoding"))
        );
    }

    private IConfigMatch named(String name) {
        return new MethodNameMatch(name);
    }
}
