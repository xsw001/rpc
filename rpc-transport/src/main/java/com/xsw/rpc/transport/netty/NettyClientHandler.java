package com.xsw.rpc.transport.netty;

import com.xsw.rpc.Request;
import com.xsw.rpc.Response;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 自定义客户端ChannelHandler处理服务端发送的数据
 * 如果继承自 SimpleChannelInboundHandler 的话就不要考虑 ByteBuf 的释放
 * 内部的 channelRead 方法会自动释放 ByteBuf ，避免可能导致的内存泄露问题
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<Response> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Response response) throws Exception {
        log.info("客户端收到消息：" + response);
        // 声明一个 AttributeKey 对象
        AttributeKey<Response> key = AttributeKey.valueOf("Response");
        // 将服务端的返回结果保存到 AttributeMap 上，AttributeMap 可以看作是一个Channel的共享数据源
        // AttributeMap的key是AttributeKey，value是Attribute
        channelHandlerContext.channel().attr(key).set(response);
        //  Channel 关闭
        channelHandlerContext.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("发送心跳包 [{}]", ctx.channel().remoteAddress());
                Channel channel = ctx.channel();
                Request request = new Request();
                request.setHeartBeat(true);
                channel.writeAndFlush(request).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else
            super.userEventTriggered(ctx, evt);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("与远程 Server 端建立连接。");

        // do something

        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("与远程 Server 端断开连接。");

        // do something

        ctx.fireChannelInactive();
    }
}
