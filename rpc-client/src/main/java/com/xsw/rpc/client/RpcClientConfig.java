package com.xsw.rpc.client;

import com.xsw.rpc.Peer;
import com.xsw.rpc.codec.*;
import com.xsw.rpc.transport.TransportClient;
import com.xsw.rpc.transport.http.HTTPTransportClient;
import com.xsw.rpc.transport.netty.NettyTransportClient;
import com.xsw.rpc.transport.netty.other.NettyClient;
import com.xsw.rpc.transport.socket.SocketTransportClient;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class RpcClientConfig {
    //需要的Client的实现类型
    // private Class<? extends TransportClient> transportClientClass = HTTPTransportClient.class;
    // private Class<? extends TransportClient> transportClientClass = SocketTransportClient.class;
    //     private Class<? extends TransportClient> transportClientClass = NettyTransportClient.class;
    private Class<? extends TransportClient> transportClientClass = NettyClient.class;

    //序列化
    //    private Class<? extends Encoder> encoderClass = JSONEncoder.class;
    //    private Class<? extends Decoder> decoderClass = JSONDecoder.class;

    private Class<? extends Encoder> encoderClass = KryoEncoder.class;
    private Class<? extends Decoder> decoderClass = KryoDecoder.class;

    //路由选择策略
    private Class<? extends TransportSelector> selectorClass = RandomTransportSelector.class;
}
