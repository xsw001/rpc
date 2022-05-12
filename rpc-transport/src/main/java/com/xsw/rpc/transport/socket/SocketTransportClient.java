package com.xsw.rpc.transport.socket;

import com.xsw.rpc.Peer;
import com.xsw.rpc.Request;
import com.xsw.rpc.Response;
import com.xsw.rpc.codec.KryoDecoder;
import com.xsw.rpc.common.utils.ReflectionUtils;
import com.xsw.rpc.register.ServiceRegistry;
import com.xsw.rpc.register.nacos.NacosServiceRegistry;
import com.xsw.rpc.transport.TransportClient;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.xsw.rpc.codec.Decoder;

@Slf4j
public class SocketTransportClient implements TransportClient {

    private Peer peer;
    private final ServiceRegistry serviceRegistry = new NacosServiceRegistry();

    @Override
    public void connect(Peer peer) {
        // 创建Socket对象并且指定服务器的地址和端口号
        this.peer = peer;
    }

    @Override
    public Object write(Request request) {
        try (Socket socket = new Socket()) {
            peer = serviceRegistry.lookup(request.getService().getClazz());
            socket.connect(new InetSocketAddress(peer.getHost(), peer.getPort()));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(request);

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            // 从输入流中读取 RpcResponse
            return objectInputStream.readObject();
        } catch (Exception e) {
            throw new IllegalStateException("客户端发送失败...", e);
        }
    }

    @Override
    public void close() {

    }
}
