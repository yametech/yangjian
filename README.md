# 杨戬(yangjian)
![build](https://github.com/yametech/yangjian/workflows/build/badge.svg) [![License](https://img.shields.io/github/license/yametech/yangjian)](/LICENSE)

yangjian-agent是一个基于javaagent运行的java性能监控工具，具备较强的扩展性与性能

## 参考

该工具开发时参考的开源项目：
* [skywalking](https://github.com/apache/skywalking)
* [MyPerf4J](https://github.com/LinShunKang/MyPerf4J )
* [druid](https://github.com/alibaba/druid )

## 功能

* 每秒调用量（QPS），已支持spring-controller、dubbo-client、dubbo-server、httpclient、okhttp、kafka、rabbitmq、mongo集合、mysql表及sql自动统计，jedis、redisson、方法配置化统计；

* 每秒平均耗时（RT），已支持spring-controller、dubbo-client、dubbo-server、httpclient、okhttp、kafka、rabbitmq、mongo集合、mysql表及sql自动统计，jedis、redisson、方法配置化统计；
* 池监控：已支持druid、hikaricp，线程池规划中；
* 进程监控：CPU、内存占用量；
* JVM相关数据收集：内存、GC；
* 【规划中】运行时jar包依赖情况；
* 【规划中】运行时资源依赖情况；
* 【规划中】基于日志的异常监控；

## 设计

* [设计文档](https://github.com/yametech/yangjian/wiki/%E8%AE%BE%E8%AE%A1%E6%96%87%E6%A1%A3 )

## Benchmark

* [性能报告](https://github.com/yametech/yangjian/wiki/%E6%80%A7%E8%83%BD%E6%8A%A5%E5%91%8A )


## 快速接入

* [部署文档](https://github.com/yametech/yangjian/wiki/%E9%83%A8%E7%BD%B2%E6%96%87%E6%A1%A3 )

## 打包

在项目根目录下执行：`mvn clean package`，根目录下生成的deploy目录文件，即为部署文件

## 扩展（接口）

创建项目并引入api包（yangjian-agent-api，该包无其他依赖），可支持以下接口自定义扩展（可参考现有插件的实现方式）：

![yangjian-agent](docs/readme-files/yangjian-agent.png)

### 快速扩展示例

场景：只监控TestService.execute(String cmd)的QPS/RT，以下为业务代码

> 注意：当前场景仅说明如何扩展插件，实际上该场景目前已有插件支持，仅需要配置拦截的类方法定义即可统计QPS/RT；

```java
package cn.xxx.plugin.test;

public class TestService {
	public void execute() {
		// 业务逻辑
		System.err.println("execute()");
	}
    public void execute(String cmd) {
		// 业务逻辑
    	System.err.println("execute(String cmd)");
	}
    public void execute(Integer index) {
		// 业务逻辑
    	System.err.println("execute(Integer index)");
	}
}
```

1、创建maven项目：agent-plugin-test（名称无要求）

2、pom包依赖及配置修改（JDK8及以上）

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <java.version>1.8</java.version>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    <maven.compiler.compilerVersion>1.8</maven.compiler.compilerVersion>
</properties>

<dependencies>
    <dependency>
        <groupId>com.github.yametech</groupId>
        <artifactId>yangjian-agent-api</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

3、实现IMetricMatcher接口，设置拦截方法

```java
package cn.xxx.plugin.test;

import java.util.Arrays;

import com.yametech.yangjian.agent.api.IMetricMatcher;
import com.yametech.yangjian.agent.api.base.IConfigMatch;
import com.yametech.yangjian.agent.api.base.MethodType;
import com.yametech.yangjian.agent.api.bean.LoadClassKey;
import com.yametech.yangjian.agent.api.configmatch.CombineAndMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodArgumentIndexMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodArgumentNumMatch;
import com.yametech.yangjian.agent.api.configmatch.MethodRegexMatch;

public class TestMatcher implements IMetricMatcher {

	@Override
	public IConfigMatch match() {
		return new CombineAndMatch(Arrays.asList(
				new MethodRegexMatch(".*cn\\.xxx\\.plugin\\.test\\.TestService\\.execute\\(.*"),// 拦截的类及方法定义
				new MethodArgumentNumMatch(1), // 方法参数个数
				new MethodArgumentIndexMatch(0, "java.lang.String")// 方法参数类型
			));
	}

	@Override
	public String type() {
		return "test-type";// 当前QPS/RT的类型，用于UI展示时分组
	}

	@Override
	public LoadClassKey loadClass(MethodType type) {
		return new LoadClassKey("cn.xxx.plugin.test.TestConvert");// 拦截后执行的逻辑
	}
}
```

4、实现IMethodConvert接口，完成方法调用转TimeEvent

```java
package cn.xxx.plugin.test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.yametech.yangjian.agent.api.bean.TimeEvent;
import com.yametech.yangjian.agent.api.convert.IMethodConvert;

public class TestConvert implements IMethodConvert {

	@Override
	public List<TimeEvent> convert(Object thisObj, long startTime, Object[] allArguments, 
			Method method, Object ret, Throwable t, Map<Class<?>, Object> globalVar) throws Throwable {
		TimeEvent event = get(startTime);
		event.setIdentify("TestService.execute(String)");
		return Arrays.asList(event);
	}
}
```

5、在项目目录/src/main/resources/META-INF/services下增加文件`com.yametech.yangjian.agent.api.base.SPI`，并添加以下内容

```
cn.xxx.plugin.test.TestMatcher
```

6、打包

```
maven clean package
```

7、复制`\target\agent-plugin-test-1.0.0-SNAPSHOT.jar`到探针包目录plugins下

8、增加一个main方法测试拦截，并按照文档中的接入-> 启动脚本 -> Main启动

> 如果在IDE下运行，配置对应IDE的启动参数，eclipse可在Run Configurations ...找到对应的Java Application，在Arguments -> VM arguments下增加：`-javaagent:\探针包目录\lib\yangjian-agent.jar -DMonitorAgent.service.name=agent-main-test`

```java
package cn.xxx.plugin.test;

public class Main {
	public static void main(String[] args) {
		TestService service = new TestService();
		service.execute();
		service.execute(1);
		service.execute("123");
		System.exit(0);
	}
}
```

9、修改log.properties的`log.output`为CONSOLE（可在控制台或者运行窗口直接看到输出日志），启动main，如果出现以下日志则拦截成功（可搜索`statistic/test-type`）

```
2020-02-07 11:08:36.515[INFO]-[schedule-2]-[c.e.t.a.c.u.LogUtil.println(71)]: agent-main-test/1581044915/statistic/test-type/RT?sign=TestService.execute%28String%29&rt_max=1&rt_min=1&num=1&rt_total=1
```

> 日志说明：[*应用名称*]/[*方法执行时的秒数*]/statistic/[*TestMatcher中配置的type*]/[RT/QPS]?sign=[*setIdentify配置的*]&rt_max=[*当前秒最大耗时毫秒数*]&rt_min=[*当前秒最小耗时毫秒数*]&num=[*当前秒总执行次数*]&rt_total=[*总耗时毫秒数*]

### IConfigMatch实现使用说明

![IConfigMatch](docs/readme-files/IConfigMatch.png)

## License

[Apache License 2.0](/LICENSE)
