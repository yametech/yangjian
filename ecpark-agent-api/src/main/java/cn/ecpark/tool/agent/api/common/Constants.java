package cn.ecpark.tool.agent.api.common;

/**
 * @author zcn
 * @date: 2019-10-15
 **/
public class Constants {

    public static final String PATH_SEPARATOR = System.getProperty("file.separator", "/");
    public static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
    public static final String SYSTEM_PROPERTIES_PREFIX = "MonitorAgent.";
//    public static String SERVICE_NAME = "service.name";

    public static final String CONFIG_PATH = "config.path";
    public static final String CONFIG_REMOTE_URL = "config.remote_url";

    public static final String LOG_OUTPUT = "log.output";
    public static final String LOG_DIR = "log.dir";
    public static final String LOG_LEVEL = "log.level";
    public static final String LOG_MAX_FILE_SIZE = "log.max_file_size";
    public static final String LOG_MAX_FILE_NUM = "log.max_file_num";
    public static final String LOG_PATTERN = "log.pattern";

    public static final String IDENTIFY_SEPARATOR = " | ";

    public static class EventType {
        public static final String MYSQL = "mysql";
        public static final String DUBBO_CLIENT = "dubbo-client";
        public static final String DUBBO_SERVER = "dubbo-server";
        public static final String HTTP_CLIENT = "http-client";
        public static final String REDIS = "redis";
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

    public static class DbOperation {
        public static final String INSERT = "Insert";
        public static final String DELETE = "Delete";
        public static final String UPDATE = "Update";
        public static final String SELECT = "Select";
        public static final String GET_CONNECTION = "GetConnection";
    }
}
