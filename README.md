# 杨戬(yangjian)

简体中文 | [English](README_EN.md)

![build](https://github.com/yametech/yangjian/workflows/build/badge.svg) ![java-version](https://img.shields.io/badge/JDK-1.8+-brightgreen.svg) ![maven](https://img.shields.io/badge/maven-3.5+-brightgreen.svg) [![License](https://img.shields.io/github/license/yametech/yangjian)](/LICENSE)

yangjian-agent是一个基于javaagent运行的java性能监控工具，具备较强的扩展性与性能

## 功能

* 每秒调用量（QPS），已支持spring-controller、dubbo-client、dubbo-server、httpclient、okhttp、kafka、rabbitmq、mongo集合、mysql表及sql自动统计，jedis、redisson、方法配置化统计；
* 每秒平均耗时（RT），已支持spring-controller、dubbo-client、dubbo-server、httpclient、okhttp、kafka、rabbitmq、mongo集合、mysql表及sql自动统计，jedis、redisson、方法配置化统计；
* 池监控：已支持druid、hikaricp；
* 进程监控：CPU、内存占用量；
* JVM相关数据收集：内存、GC；

## 规划中

* 运行时jar包依赖情况；
* 运行时资源依赖情况；
* 基于日志的异常监控；
* 线程池监控；

## 设计

* [设计文档](https://github.com/yametech/yangjian/wiki/%E8%AE%BE%E8%AE%A1%E6%96%87%E6%A1%A3 )

## Benchmark

* [性能报告](https://github.com/yametech/yangjian/wiki/%E6%80%A7%E8%83%BD%E6%8A%A5%E5%91%8A )


## 快速接入

* [部署文档](https://github.com/yametech/yangjian/wiki/%E9%83%A8%E7%BD%B2%E6%96%87%E6%A1%A3 )

## 打包

在项目根目录下执行：`mvn clean package -Dmaven.javadoc.skip=true -DskipTests=true`，根目录下生成的deploy目录文件，即为部署文件

## 扩展（接口）

请查看[开发文档](https://github.com/yametech/yangjian/wiki/%E5%BC%80%E5%8F%91%E6%96%87%E6%A1%A3)

## 参考项目

感谢以下开源项目提供较好的开发思路：
* [skywalking](https://github.com/apache/skywalking)
* [MyPerf4J](https://github.com/LinShunKang/MyPerf4J )
* [druid](https://github.com/alibaba/druid )

## License

[Apache License 2.0](/LICENSE)
