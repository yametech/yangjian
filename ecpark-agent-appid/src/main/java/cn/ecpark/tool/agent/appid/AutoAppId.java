package cn.ecpark.tool.agent.appid;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于自动获取并设置应用标识
 *
 * @author liuzhao
 * @Description
 * @date 2019年12月25日 下午12:40:13
 */
public class AutoAppId {
    private static final String[] RESOURCES = new String[]{"", "/", "META-INF"};
    private static final String[] SERVICE_NAME_KEYS = new String[]{"skywalking.agent.service_name", "MonitorAgent.service.name"};
    private static final String[] APPNAME_PREFIX = new String[]{"rpc-", "mic-", "tom-", "asm-", "cus-"};// , "tool-"
    private static final Pattern SERVICE_NAME_PATTERN = Pattern.compile("(file:)?/data/www/apps/([\\w-]*)[/]?");
    /**
     * docker环境下应用名称所在的文件路径
     */
    private static final String DOCKER_APP_NAME_PATH = "/mnt/app_name.txt";

    /**
     * -javaagent:E:\eclipse-workspace\tool-ecpark-monitor\ecpark-agent\dist\ecpark-agent\ecpark-agent.jar=args -Dskywalking.agent.service_name=testlog
     *
     * @param arguments       javaagent=号后的文本，例如：-javaagent:E:\eclipse-workspace\tool-ecpark-monitor\ecpark-agent\target\ecpark-agent.jar=arg=123，此时arguments=arg=123
     * @param instrumentation
     * @throws Exception
     */
    public static void premain(String arguments, Instrumentation instrumentation) throws Exception {
        String serviceName = null;
        for (String key : SERVICE_NAME_KEYS) {
            if (System.getProperty(key) != null && System.getProperty(key).trim().length() > 0) {
                serviceName = System.getProperty(key);
                break;
            }
        }
        if (serviceName == null) {
            serviceName = getServiceName();
        }
        if (serviceName == null) {
            return;
        }
        for (String key : SERVICE_NAME_KEYS) {
            if (System.getProperty(key) == null) {
                System.setProperty(key, serviceName);
                println("setProperty " + key + " to " + serviceName);
            }
        }
    }

    private static String getServiceName() {
        URL resource = null;
        for (String r : RESOURCES) {
            resource = Thread.currentThread().getContextClassLoader().getResource(r);
            if (resource != null) {
                break;
            }
        }
        if (resource == null) {
            return null;
        }
        String appPath = resource.getPath();
        if (appPath == null) {
            return null;
        }
        println("appPath=" + appPath);
        String serviceName = null;
        Matcher matcher = SERVICE_NAME_PATTERN.matcher(appPath);
        while (matcher.find()) {
            serviceName = matcher.group(2);
            for (String prefix : APPNAME_PREFIX) {
                if (serviceName.startsWith(prefix)) {
                    serviceName = serviceName.substring(prefix.length());
                    break;
                }
            }
        }

        // 如果获取不到尝试docker环境文件的获取
        if (serviceName == null) {
            try {
                File file = new File(DOCKER_APP_NAME_PATH);
                if (file.exists()) {
                    serviceName = new String(Files.readAllBytes(file.toPath())).trim();
                } else {
                    println(DOCKER_APP_NAME_PATH + " not found.");
                }
            } catch (Exception e) {
                // ignore
            }
        }
        println("serviceName=" + (serviceName == null ? "null" : serviceName));
        return serviceName;
    }

    private static void println(String msg) {
        String dataStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        System.out.println(dataStr + "[INFO]-[" + Thread.currentThread().getName() + "]-[" + AutoAppId.class.getName() + "]: " + msg);
    }
}
