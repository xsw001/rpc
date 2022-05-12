package com.xsw.nettyIdle.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * <p>收到来自客户端的数据包后, 直接在控制台打印出来.</p>
 */
@ChannelHandler.Sharable
public class ServerBizHandler extends SimpleChannelInboundHandler<String> {

    private final String REC_HEART_BEAT = "I had received the heart beat!";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String data) throws Exception {
        try {
            System.out.println("receive data: " + data);
//            ctx.writeAndFlush(REC_HEART_BEAT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("与远程客户端建立连接。");

        // do something

        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("与远程客户端断开连接。");

        // do something

        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
