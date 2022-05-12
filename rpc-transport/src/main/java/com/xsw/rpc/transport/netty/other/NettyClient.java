package com.xsw.rpc.transport.netty.other;

import com.xsw.rpc.Peer;
import com.xsw.rpc.Request;
import com.xsw.rpc.Response;
import com.xsw.rpc.codec.KryoEncoder;
import com.xsw.rpc.common.factory.SingletonFactory;
import com.xsw.rpc.register.ServiceRegistry;
import com.xsw.rpc.register.nacos.NacosServiceRegistry;
import com.xsw.rpc.transport.TransportClient;
import com.xsw.rpc.transport.netty.CommonDecoder;
import com.xsw.rpc.transport.netty.CommonEncoder;
import com.xsw.rpc.transport.netty.NettyClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyClient implements TransportClient {

    private final ServiceRegistry serviceRegistry;
    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;
    private final UnprocessedRequests unprocessedRequests;

    public NettyClient() {
        this.serviceRegistry = new NacosServiceRegistry();
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    // 直接配置好 Netty 客户端
    static {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class);
    }

    @Override
    public void connect(Peer peer) {
    }

    @Override
    public Object write(Request request) {
        CompletableFuture<Response> resultFuture = new CompletableFuture<>();
        try {
            Peer peer = serviceRegistry.lookup(request.getService().getClazz());
            log.info("客户端连接到服务器 {}:{}", peer.getHost(), peer.getPort());
            Channel channel = ChannelProvider.get(new InetSocketAddress(peer.getHost(),peer.getPort()), request.getSerializationId());
            if (channel != null) {
                if (!channel.isActive()) {
                    eventLoopGroup.shutdownGracefully();
                    return null;
                }
                unprocessedRequests.put(request.getRequestId(), resultFuture);
                channel.writeAndFlush(request).addListener((ChannelFutureListener) future1 -> {
                    if (future1.isSuccess()) {
                        log.info("客户端发送消息：" + request.toString());
                    } else {
                        future1.channel().close();
                        resultFuture.completeExceptionally(future1.cause());
                        log.error("发送消息失败，因为：" + future1.cause());
                    }
                });
            }
        } catch (InterruptedException e) {
            unprocessedRequests.remove(request.getRequestId());
            log.error("发送消息时有错误发生: ", e);
            Thread.currentThread().interrupt();
        }
        Response response = null;
        try {
            response = resultFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(response);
        return response;
    }

    @Override
    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}
