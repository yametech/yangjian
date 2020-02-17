package cn.ecpark.tool.agent.appid;

import java.io.File;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	private static final String[] APPNAME_PREFIX = new String[]{"rpc-", "mic-", "tom-", "asm-", "cus-"};// , "tool-"
	/**
	 * docker环境下应用名称所在的文件路径
	 */
	private static final String DOCKER_APP_NAME_PATH = "/mnt/app_name.txt";

	/**
	 * 测试javaagent
	 * 		-javaagent:E:\eclipse-workspace\tool-ecpark-monitor\ecpark-agent\target\ecpark-agent.jar
	 * 
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		System.err.println(AutoAppId.class);
		System.err.println(AutoAppId.class.getName());
		System.err.println(AutoAppId.class.getClass());
		
//		System.exit(0);

		String path = "file:/data/www/apps/ecpark-monitor-service_sfsf/sfd";
		Pattern pattern = Pattern.compile("(file:)?/data/www/apps/([\\w-]*)[/]?");
		Matcher matcher = pattern.matcher(path);
		while (matcher.find()) {
			String serviceName = matcher.group(2);
			for (String prefix : APPNAME_PREFIX) {
				if (serviceName.startsWith(prefix)) {
					serviceName = serviceName.substring(prefix.length());
				}
			}
			System.out.println("========>" + serviceName);
		}

		try {
			File file = new File(DOCKER_APP_NAME_PATH);
			if (file.exists()) {
				System.out.println(new String(Files.readAllBytes(file.toPath())).trim());
			} else {
				System.out.println("==================not found");
			}
		} catch (Exception e) {}
	}
	
}
