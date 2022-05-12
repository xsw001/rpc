package com.xsw.rpc.server;

import com.xsw.rpc.codec.*;
import com.xsw.rpc.register.ServiceRegistry;
import com.xsw.rpc.register.nacos.NacosServiceRegistry;
import com.xsw.rpc.transport.TransportServer;
import com.xsw.rpc.transport.http.HTTPTransportServer;
import com.xsw.rpc.transport.netty.NettyTransportServer;
import com.xsw.rpc.transport.socket.SocketTransportServer;
import lombok.Data;

/**
 * server配置
 */
@Data
public class RpcServerConfig {

    // 使用的网络模块
    // private Class<? extends TransportServer> transportClass = HTTPTransportServer.class;
    // private Class<? extends TransportServer> transportClass = SocketTransportServer.class;
    private Class<? extends TransportServer> transportClass = NettyTransportServer.class;

    // 使用的序列化实现
    // private Class<? extends Encoder> encoder = JSONEncoder.class;
    // private Class<? extends Decoder> decoder = JSONDecoder.class;

    private Class<? extends Encoder> encoder = KryoEncoder.class;
    private Class<? extends Decoder> decoder = KryoDecoder.class;

    // 监听端口
    private String host = "127.0.0.1";
    private Integer port = 4643;

    // 服务注册中心
    private Class<? extends ServiceRegistry> serviceRegistry = NacosServiceRegistry.class;
}
