import com.xsw.rpc.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

public class MyClient {
    public static void main(String[] args) throws InterruptedException {
        //创建两个线程池组
        NioEventLoopGroup group = new NioEventLoopGroup();

        //创建Netty启动类
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
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
                        pipeline.addLast(new NettyClientHandler());
                    }
                });
        ChannelFuture future = bootstrap.connect("127.0.0.1", 7000).sync();
        Channel channel = future.channel();
        if (channel != null) {
            channel.writeAndFlush(Unpooled.copiedBuffer("天上人间", CharsetUtil.UTF_8)).addListener(future1 -> {
                if (future1.isSuccess()) {
                    System.out.println("客户端发送消息: 天上人间");
                } else {
                    System.out.println("发送消息时有错误发生: " + future1.cause());
                }
            });
        }
    }

}

class NettyClientHandler extends ChannelInboundHandlerAdapter {
    //当通道就绪就会触发该方法
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client" + ctx);
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello,Server:小沙弥", CharsetUtil.UTF_8));
    }

    //当通道有读取事件是，会触发
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("服务器回复的消息：" + buf.toString(CharsetUtil.UTF_8));
        System.out.println("服务器地址是：" + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}



