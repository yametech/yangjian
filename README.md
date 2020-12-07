# yangjian

[简体中文](README_CN.md) | English

![build](https://github.com/yametech/yangjian/workflows/build/badge.svg) ![java-version](https://img.shields.io/badge/JDK-1.8+-brightgreen.svg) ![maven](https://img.shields.io/badge/maven-3.5+-brightgreen.svg) [![License](https://img.shields.io/github/license/yametech/yangjian)](/LICENSE)

A Java performance monitoring tool based on JavaAgent, with strong scalability and performance.

## Features

* QPS/RT：Spring Controller, Dubbo Client, Dubbo Server, HttpClient, OkHTTP, Kafka, RabbitMQ, Mongo collection, MySql Table/SQL, Jedis, Redisson, Custom Method;
* Pool monitoring：druid, hikaricp;
* Process monitoring：CPU, memory;
* JVM monitoring：memory, GC;

## TODO

* JAR dependencies at runtime；
* Resource dependencies at runtime；
* Log exception monitoring；
* Thread pool monitoring；

## Design

* [Document](https://github.com/yametech/yangjian/wiki/%E8%AE%BE%E8%AE%A1%E6%96%87%E6%A1%A3 )

## Benchmark

* [Document](https://github.com/yametech/yangjian/wiki/%E6%80%A7%E8%83%BD%E6%8A%A5%E5%91%8A )


## Quick start

* [Document](https://github.com/yametech/yangjian/wiki/%E9%83%A8%E7%BD%B2%E6%96%87%E6%A1%A3 )

## Build

Execute `mvn clean package -Dmaven.javadoc.skip=true -DskipTests=true` in the project root path. The `deploy` directory file, which is generated in the root directory, is the deployment file.

The build process requires `JDK8+`.
## Extend（Interface）

[Document](https://github.com/yametech/yangjian/wiki/%E5%BC%80%E5%8F%91%E6%96%87%E6%A1%A3)

## Inspired by
* [skywalking](https://github.com/apache/skywalking)
* [MyPerf4J](https://github.com/LinShunKang/MyPerf4J )
* [druid](https://github.com/alibaba/druid )

## License

[Apache License 2.0](/LICENSE)
