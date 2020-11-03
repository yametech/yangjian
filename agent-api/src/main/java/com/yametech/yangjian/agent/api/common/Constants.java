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
package com.yametech.yangjian.agent.api.common;

/**
 * @author zcn
 */
public class Constants {

    public static final String PATH_SEPARATOR = System.getProperty("file.separator", "/");
    public static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
    public static final String SYSTEM_PROPERTIES_PREFIX = "MonitorAgent.";
    public static final String SERVICE_NAME = "service.name";
    public static final String SERVICE_ENV = "service.env";
    public static final String AGENT_VERSION = "agent.version";
    public static final String DEFAULT_SERVICE_ENV = "dev";
    public static final String ENV_PRO = "pro";
    public static final String ENV_PROD = "prod";

    public static final String CONFIG_PATH = "config.path";
    public static final String CONFIG_REMOTE_URL = "config.remote_url";

    public static final String EXTEND_PLUGINS_DIR = "extend.plugins.dir";

    public static final String LOG_OUTPUT = "log.output";
    public static final String LOG_DIR = "log.dir";
    public static final String LOG_TRACE_DIR = "log.trace.dir";
    public static final String LOG_LEVEL = "log.level";
    public static final String LOG_MAX_FILE_SIZE = "log.max_file_size";
    public static final String LOG_MAX_FILE_NUM = "log.max_file_num";
    public static final String LOG_PATTERN = "log.pattern";

    public static final String IDENTIFY_SEPARATOR = " | ";
    public static final String RABBITMQ_DEFAULT_EXCHANGE = "Default";

    public static final String REPORT_CONFIG_KEY_PREFIX = "report";
    public static final String DISABLE_SPI_KEY_PREFIX = "spi.";
    public static final String DEPENDENCY_PATH = "dependency/";

    public static final int MAX_SPAN_NAME_LENGTH = 255;
    public static final int MAX_TAG_LENGTH = 255;

    public static class Status {
    	public static final String STARTING = "starting";// 服务启动中
    	public static final String STARTED = "started";// 服务已启动
    	public static final String RUNNING = "running";// 服务启动心跳
    	public static final String STOPPING = "stopping";// 服务关闭中
    	public static final String STOPPED = "stopped";// 服务已关闭
    	public static final String RESOURCES = "resources";// 资源状态：jvm...
    }

    public static class EventType {
        public static final String MYSQL = "mysql";
        public static final String DUBBO_CLIENT = "dubbo-client";
        public static final String DUBBO_SERVER = "dubbo-server";
        public static final String HTTP_CLIENT = "http-client";
        public static final String REDIS = "redis";
        public static final String REDIS_KEY = "redis-key";
        public static final String REDIS_COMMAND = "redis-command";
        public static final String KAFKA_CONSUME = "kafka-consume";
        public static final String KAFKA_PUBLISH = "kafka-publish";
        public static final String RABBITMQ_CONSUME = "rabbitmq-consume";
        public static final String RABBITMQ_PUBLISH = "rabbitmq-publish";
        public static final String HTTP_SERVER = "http-server";
        public static final String METHOD = "method";
        public static final String MONGO = "mongo";
        public static final String DRUID = "druid";
        public static final String HIKARICP = "hikaricp";
        public static final String MYSQL_TABLE = "mysql-table";
        public static final String MYSQL_SQL = "mysql-sql";
        public static final String CUSTOM = "custom";
    }

    public static class ReportType {
    	public static final String LOG = "log";// log上报方式
    	public static final String HTTP = "http";// http上报方式
        public static final String LOG_TRACE = "log-trace";// trace单独日志文件
    }

    public static class DbOperation {
        public static final String INSERT = "Insert";
        public static final String DELETE = "Delete";
        public static final String UPDATE = "Update";
        public static final String SELECT = "Select";
        public static final String GET_CONNECTION = "GetConnection";
    }

    public static String serviceName() {
    	return System.getProperty(SYSTEM_PROPERTIES_PREFIX + SERVICE_NAME);
    }

    public static class Tags {
        public static final String STATUS_CODE = "status_code";
        public static final String URL = "url";
        public static final String HTTP_METHOD = "http.method";
        public static final String MQ_TOPIC = "mq.topic";
        public static final String MQ_SERVER = "mq.broker";
        public static final String MQ_CONSUMER = "mq.consumer";
        // rabbitmq使用
        public static final String MQ_QUEUE = "mq.queue";

        public static final String DB_INSTANCE = "db.instance";
        public static final String DB_STATEMENT = "db.statement";
        public static final String DB_TYPE = "db.type";
        public static final String DB_PARAMETERS = "db.parameters";

        public static final String PEER = "_peer";
        public static final String COMPONENT = "_component";
        public static final String DATABASE = "_database";
        public static final String PARENT_SERVICE_NAME = "_parent_service_name";
        public static final String AGENT_SIGN = "_parent_agent_sign";
        // 部署模式 如：Redis的单机模式single、集群模式cluster、哨兵sentinel
        public static final String DB_MODE = "_mode";
        public static final String DUBBO_GROUP = "dubbo.group";
    }

    public static class Component {
        public static final String DUBBO = "dubbo";
        public static final String OKHTTP = "okhttp";
        public static final String JEDIS = "jedis";
        public static final String MYSQL_JDBC = "mysql-jdbc";
        public static final String KAFKA = "kafka";
        public static final String RABBITMQ = "rabbitmq";
        public static final String HTTP_CLIENT = "httpclient";
        public static final String REDISSON = "redisson";
        public static final String LETTUCE = "lettuce";
        public static final String MONGO = "mongodb";
        public static final String SPRING_WEBFLUX = "spring-webflux";
        public static final String SPRING_MVC = "spring-mvc";
        public static final String JFINAL = "jfinal";
        public static final String SPRING_RESTTEMPLATE = "spring-resttemplate";
        public static final String SPRING_WEBCLIENT = "spring-webclient";
    }

    /**
     * 自定义请求头部key（在请求链路中传递）
     */
    public static class ExtraHeaderKey {
        public static final String USER_ID = "x-user-id";
        public static final String REFERER_SERVICE = "x-referer-service";
        // 探针唯一标识
        public static final String AGENT_SIGN = "x-agent-sign";
    }

    /**
     * 部署模式（目前redis组件使用）
     */
    public static class DbMode {
        // 集群模式
        public static final String CLUSTER = "cluster";
        // 哨兵模式
        public static final String SENTINEL = "sentinel";
        // Redis节点模式
        public static final String SINGLE = "single";
        // 云托管模式(Redisson使用)
        public static final String REPLICATED = "replicated";
        // 主从模式
        public static final String MASTER_SLAVE = "master-slave";
    }
}
