package com.xsw.rpc.client;

import com.xsw.rpc.Peer;
import com.xsw.rpc.transport.TransportClient;

import java.util.List;

// 选择哪个server去连接
public interface TransportSelector {
    public static final int a = 0;

    /**
     * 初始化selector
     * @param peers 可以连接的server端点信息
     * @param count client 与 server 建立多少个连接
     * @param clazz client的实现 class
     */
    void init(List<Peer> peers, int count, Class<? extends TransportClient> clazz);

    /**
     * 选择一个transport与server做交互
     *
     * @return 网络client
     */
    TransportClient select();

    /**
     * 释放用完的client
     *
     * @param client 网络
     */
    void release(TransportClient client);

    void close();

}
