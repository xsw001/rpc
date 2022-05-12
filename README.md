# RPC
用于学习的【基于 Java 的轻量级 RPC 框架】

项目描述：实现轻量级 RPC 框架，使得客户端可以通过网络从远程服务端程序上请求服务

主要实现：

基于 Nacos 的服务注册与发现，实现服务自动注册与自动注销

使用 JDK 动态代理，基于 Netty 实现网络通信，自定义协议与编解码器，实现心跳机制

三种序列化算法：JSON 方式、Kryo 算法、Hessian 算法

基于随机算法、轮转算法、一致性 Hash 算法的负载均衡策略
