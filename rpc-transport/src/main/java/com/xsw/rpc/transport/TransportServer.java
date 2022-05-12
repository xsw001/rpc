package com.xsw.rpc.transport;

import com.xsw.rpc.register.local.ServiceInvoker;
import com.xsw.rpc.register.local.LocalServiceProvider;

/**
 * 1、启动，监听
 * 2、等待连接，接受请求后处理
 * 接受的请求只是一个InputStream（byte数据流）————>  抽象成一个 handler 让使用者做实现
 * 3、关闭监听
 */
public interface TransportServer {
    void init(String host, int port, RequestHandler handler);

    void start();

    void stop();
}
