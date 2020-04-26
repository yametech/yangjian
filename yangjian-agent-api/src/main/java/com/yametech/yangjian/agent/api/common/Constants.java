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
 **/
public class Constants {

    public static final String PATH_SEPARATOR = System.getProperty("file.separator", "/");
    public static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
    public static final String SYSTEM_PROPERTIES_PREFIX = "MonitorAgent.";
    public static final String SERVICE_NAME = "service.name";

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

    public static class Status {
    	public static final String STARTING = "starting";// 服务启动中
    	public static final String STARTED = "started";// 服务已启动
    	public static final String HEARTBEAT = "heartbeat";// 服务启动心跳
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
    }
    
    public static class ReportType {
    	public static final String LOG = "log";// log上报方式
    	public static final String HTTP = "http";// http上报方式
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
}
