package com.xsw.rpc.transport.netty;

import com.xsw.rpc.Peer;
import com.xsw.rpc.codec.KryoEncoder;
import com.xsw.rpc.register.ServiceRegistry;
import com.xsw.rpc.register.local.LocalServiceProvider;
import com.xsw.rpc.register.nacos.NacosServiceRegistry;
import com.xsw.rpc.register.nacos.hook.ShutdownHook;
import com.xsw.rpc.transport.RequestHandler;
import com.xsw.rpc.transport.TransportServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyTransportServer implements TransportServer {

    private String host;
    private int port;

    @Override
    public void init(String host, int port, RequestHandler handler) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void start() {
        // 注册这个【自动注销服务】的钩子, 服务端关闭之前自动向 Nacos 注销服务
        ShutdownHook.getShutdownHook().addClearRegistryHook();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    // 指定 IO 模型
                    // Channel 接口是 Netty 对网络操作抽象类。通过 Channel 我们可以进行 I/O 操作
                    .channel(NioServerSocketChannel.class)
                    // (非必备)打印日志
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG, 256)
                    // 是否开启 TCP 底层心跳机制
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 当客户端第一次进行请求的时候才会进行初始化, childHandler(ChannelHandler childHandler)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            // 30 秒之内没有收到客户端请求的话就关闭连接
                            pipeline.addLast(new IdleStateHandler(30, 0, 0))
                                    .addLast(new CommonEncoder())
                                    .addLast(new CommonDecoder())
                                    .addLast(new NettyServerHandler());
                        }
                    });
            // ServerBootstrap通常使用 bind() 方法绑定本地的端口上，然后等待客户端的连接
            // bind()是异步的，通过 sync()方法将其变为同步。
            ChannelFuture future = bootstrap.bind(port).sync();
            // 阻塞等待直到服务器Channel关闭
            // closeFuture()方法获取Channel 的CloseFuture对象,然后调用sync()方法
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("启动服务器时有错误发生: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {

    }
}
