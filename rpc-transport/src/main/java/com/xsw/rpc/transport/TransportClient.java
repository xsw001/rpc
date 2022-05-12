package com.xsw.rpc.transport;

import com.xsw.rpc.Peer;
import com.xsw.rpc.Request;

import java.io.InputStream;

/**
 * 1、创建连接
 * 2、发送数据，等待响应
 * 3、关闭连接
 */
public interface TransportClient {

    void connect(Peer peer);

    Object write(Request obj);

    void close();
}
