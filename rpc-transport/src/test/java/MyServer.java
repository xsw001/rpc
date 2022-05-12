import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

public class MyServer {
    public static void main(String[] args) throws InterruptedException {
        //创建两个线程池组
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        //创建Netty启动类
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))//在bossGroup中加入一个handler
                    .childHandler(new ChannelInitializer<SocketChannel>() {//在workGroup中加入handler
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //加入一个netty 提供IdleStateHandler
                             /*
                             文档说明
                             * 1、IdleStateHandler 是netty 提供的处理空闲状态的处理器
                               2、long readerIdleTime :表示多长时间没有读，就会发送一个心跳检测包  检测是否连接
                               3、long writeIdleTime :表示多长时间没有写，就会发送一个心跳检测包  检测是否连接
                               4、long allIdleTime: 表示多长时间没有读写，就会发送一个心跳检测包  检测是否连接
                               5、当IdleStateEvent触发后，就会传递给管道（pipeline）的下一个handler去处理
                                  通过调用（触发）下一个handler的userEventTiggered,在该方法中去处理（读空闲、写空闲、读写空闲）
                             */
                            pipeline.addLast(new IdleStateHandler(3, 5, 7, TimeUnit.SECONDS));
                            //加入一个对空闲检测进一步处理的handler(自定义)
                            pipeline.addLast(new MyServerHandler());

                        }
                    });
            //启动服务器
            ChannelFuture channelFuture = serverBootstrap.bind(7000).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }


    }

}

class MyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * @param ctx 上下文
     * @param evt 事件
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            //將evt向下转型 IdleStateEvent
            IdleStateEvent event = (IdleStateEvent) evt;
            String eventType = null;
            switch (event.state()) {
                case READER_IDLE:
                    eventType = "读空闲";
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    break;
                case ALL_IDLE:
                    eventType = "读写空闲";
                    break;
            }
            //当管道发生空闲事件时，关闭通道
            ctx.channel().close().sync();
            System.out.println(ctx.channel().remoteAddress() + "--超时事件--" + eventType);
            System.out.println("服务器做相应处理..");
        }
    }
}



