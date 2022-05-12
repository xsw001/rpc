package com.xsw.rpc.transport.netty;

import com.xsw.rpc.Peer;
import com.xsw.rpc.Request;
import com.xsw.rpc.Response;
import com.xsw.rpc.codec.KryoEncoder;
import com.xsw.rpc.register.ServiceRegistry;
import com.xsw.rpc.register.nacos.NacosServiceRegistry;
import com.xsw.rpc.transport.TransportClient;
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

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyTransportClient implements TransportClient {

    private Peer peer;
    private final ServiceRegistry serviceRegistry;
    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;

    public NettyTransportClient() {
        this.serviceRegistry = new NacosServiceRegistry();
    }

    // 直接配置好 Netty 客户端
    static {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(); // EpollEventLoopGroup
        bootstrap.group(eventLoopGroup)
                // Channel 接口是 Netty 对网络操作抽象类。通过 Channel 我们可以进行 I/O 操作
                .channel(NioSocketChannel.class)
                // 打印日志(非必备)
                .handler(new LoggingHandler(LogLevel.INFO))
                //  连接的超时时间。 如果超过此时间或无法建立连接，则连接失败。
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                // 通过 .childHandler()给引导类创建一个 ChannelInitializer
                // 然后指定了客户端消息的业务处理逻辑 NettyClientHandler 对象
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        // 如果 5 秒内没有数据发送到服务器，则发送心跳请求
                        pipeline.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                                .addLast(new CommonDecoder()) // Response -> ByteBuf
                                .addLast(new CommonEncoder()) // ByteBuf -> Request
                                .addLast(new NettyClientHandler());
                    }
                });

    }

    @Override
    public void connect(Peer peer) {
        this.peer = peer;
    }

    /**
     * 发送消息到服务端
     * 1 首先初始化了一个 Bootstrap
     * 2 通过 Bootstrap 对象连接服务端
     * 3 通过 Channel 向服务端发送消息 Request
     * 4 发送成功后，阻塞等待 ，直到Channel关闭
     * 5 拿到服务端返回的结果 RpcResponse
     *
     * @param request 客户端的请求
     * @return 服务端返回的数据
     */
    @Override
    public Object write(Request request) {
        try {
            Peer peer = serviceRegistry.lookup(request.getService().getClazz());
            ChannelFuture future = bootstrap.connect(peer.getHost(), peer.getPort()).sync();
            log.info("客户端连接到服务器 {}:{}", peer.getHost(), peer.getPort());
            Channel channel = future.channel();
            if (channel != null) {
                channel.writeAndFlush(request).addListener(task -> {
                    if (task.isSuccess()) {
                        log.info("客户端发送消息：" + request.toString());
                    } else {
                        log.error("发送消息失败，因为：" + task.cause());
                    }
                });
                // 阻塞等待 ，直到 Channel 关闭
                // 通道关闭导致 netty 心跳机制失效
                channel.closeFuture().sync();
                // 通过 AttributeKey 的方式阻塞获得返回结果   key 就是 nettyClientHandler 中放进去的
                AttributeKey<Response> key = AttributeKey.valueOf("Response");
                return channel.attr(key).get();
            }
        } catch (InterruptedException e) {
            log.error("发送消息时有错误发生: ", e);
        }
        return null;
    }

    @Override
    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}
