![MIT](https://img.shields.io/badge/License-Apache2.0-blue.svg) ![JDK](https://img.shields.io/badge/JDK-8+-green.svg) ![SpringBoot](https://img.shields.io/badge/Srping%20Boot-2.3.0+-green.svg) ![redis](https://img.shields.io/badge/Redis-5.0+-green.svg) ![rebbitmq](https://img.shields.io/badge/RabbitMQ-3.8.0+-green.svg)![rocketmq](https://img.shields.io/badge/RocketMQ-4.0+-green.svg)

eventbus基于Spring Boot Starter的分布式业务消息分发总线组件（发布/订阅模式），支持延时消息。可使用Redis、RabbitMQ、RocketMQ等任意一种做底层的消息引擎，🔝 🔝 🔝点个Star关注更新。

## eventbus简介

eventbus是分布式业务消息分发总线组件，支持广播及时消息、延时消息等（即发布/订阅模式）。组件通过屏蔽底层不同种类的消息引擎，并提供统一的接口调用，可发送广播及时消息和延时消息，同时可订阅及时消息或延时消息等。当我们的应用引入eventbus组件时可降低系统耦合度。目前可选择基于Redis、RabbitMQ、RocketMQ等任意一种做底层的消息引擎，其他消息引擎中间件将被陆续支持。

注意：它不属于`消息中间件`，它是通过和消息中间件进行整合，来完成服务之间的消息通讯，类似于消息代理。

## 支持的消息中间件

- Redis

## 有哪些特点

我们不是另外开发一个MQ，而是屏蔽底层不同种类的消息中间件，并提供统一的接口调用，旨在提供简单的事件处理编程模型，让基于事件的开发更灵活简单，结构清晰易于维护，扩展方便，集成使用更简单。

## 有哪些功能

- 消息：支持广播消息、延时消息的投递和接收，支持通过多种方式订阅消息，可通过统一的接口或注解方式去订阅接收消息；
- 失败重试：支持消息投递失败时投递重试，可自定义失败重试投递次数及下次投递时间；
- 拦截器：支持全局拦截器，可自主实现拦截逻辑，支持发送前拦截（`SendBeforeInterceptor `）、发送后拦截（`SendAfterInterceptor `
  ）、投递成功后拦截（`DeliverSuccessInterceptor `）、投递失败时拦截（`DeliverThrowableEveryInterceptor ` `DeliverThrowableInterceptor `）；
- 消息轮询：通过注解[@Polling](./eventbus-core/src/main/java/com/github/likavn/eventbus/core/annotation/Polling.java)  定义消息轮询；
- 提供消息持久化示例，可参考[BsHelper](./eventbus-demo/springboot-demo/src/main/java/com/github/likavn/eventbus/demo/service/BsHelper.java)，持久化消息投递状态，可便于后续处理；
- 可控的消息订阅监听器开关，如通过`Nacos`下线某个服务实例时需要同时关闭消息的监听；
- 消息中间件网络断开重连机制，支持重连；

## 有哪些场景可以使用

- 单一业务分发消息进行异步处理时，比如业务完成推送业务数据给第三方；
- 支付时，后端服务需要定时轮训支付接口查询是否支付成功（可配置消息轮询）；
- 系统业务消息传播解耦，降低消息投递和接收的复杂度（消息可靠性传递）；
- 当我们需要切换消息中间件时，可以做到无缝切换，不需要修改业务代码；

## 版本要求

1. SpringBoot 2.3.0+
2. Redis 5.0+

## 快速开始

### 引入依赖

项目中必须引入`eventbus-spring-boot-starter`组件依赖

```xml
<!-- 必须引入 eventbus-spring-boot-starter组件-->
<dependency>
    <groupId>com.github.likavn</groupId>
  <artifactId>eventbus-spring-boot-starter</artifactId>
  <version>2.3.3</version>
</dependency>
```

`Json`序列化工具支持`Fast2json`、`Fastjson`、`Jackson`、`Gson`等任意一种。项目已引入`spring-boot-starter-web`时自带`Jackson`依赖，不需要单独引入其他`Json`工具依赖。如果项目中存在多个`Json`序列化工具依赖，序列化时的优先级如下:

```xml
<!-- 各JSON序列化工具 任选一个-->
<!-- fastjson2 -->
<dependency>
    <groupId>com.alibaba.fastjson2</groupId>
    <artifactId>fastjson2</artifactId>
    <version>${version}</version>
</dependency>
<!-- fastjson -->
<dependency>
     <groupId>com.alibaba</groupId>
     <artifactId>fastjson</artifactId>
     <version>${version}</version>
</dependency>
<!-- jackson 如果项目已引入spring-boot-starter-web，项目自带jackson依赖，不需要单独引入-->
<dependency>
     <groupId>com.fasterxml.jackson.core</groupId>
     <artifactId>jackson-databind</artifactId>
     <version>${version}</version>
</dependency>
<!-- gson -->
<dependency>
     <groupId>com.google.code.gson</groupId>
     <artifactId>gson</artifactId>
     <version>${version}</version>
</dependency>
```
