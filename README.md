### :sparkles: 基于 Java 的轻量级 RPC 框架 :sparkles: 

这是一个基于 Java 语言实现的轻量级 RPC 框架，可以实现客户端从远程服务端程序请求服务。

#### 主要实现

* 基于 Nacos 的服务注册与发现，实现服务自动注册与自动注销
* 使用 JDK 动态代理，基于 Netty 实现网络通信，自定义协议与编解码器，实现心跳机制
* 三种序列化算法：JSON 方式、Kryo 算法、Hessian 算法
* 基于随机算法、轮转算法、一致性 Hash 算法的负载均衡策略

#### 如何使用

* 将项目克隆到本地
* 编译并安装
* 在代码中引入 RPC 框架
* 实现服务接口
* 配置项目
* 启动服务
* 在客户端调用服务

#### 技术栈

* Java
* Netty
* Nacos
* Kryo
* Hessian

#### 结语

这个项目提供了一种可靠、高效的 RPC 框架，帮助开发者快速构建分布式应用程序。如果您有任何疑问，可以在 [Issues](https://github.com/xxx/xxx/issues) 提出，欢迎大家一起讨论。
