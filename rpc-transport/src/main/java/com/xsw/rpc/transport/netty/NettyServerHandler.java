package com.xsw.rpc.transport.netty;

import com.xsw.rpc.Request;
import com.xsw.rpc.Response;
import com.xsw.rpc.common.utils.ReflectionUtils;
import com.xsw.rpc.common.utils.ThreadPoolFactoryUtil;
import com.xsw.rpc.register.local.ServiceInstance;
import com.xsw.rpc.register.local.ServiceInvoker;
import com.xsw.rpc.register.local.LocalServiceProvider;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

/**
 * 如果继承自 SimpleChannelInboundHandler 的话就不要考虑 ByteBuf 的释放,
 * {@link SimpleChannelInboundHandler} 内部的 channelRead 方法会释放 ByteBuf ，避免可能导致的内存泄露问题。
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<Request> {
    private final LocalServiceProvider localServiceProvider;
    private final ServiceInvoker serviceInvoker;
    private final ExecutorService threadPool;

    public NettyServerHandler() {
        serviceInvoker = ReflectionUtils.newInstance(ServiceInvoker.class);
        localServiceProvider = new LocalServiceProvider();
        threadPool = ThreadPoolFactoryUtil.createCustomThreadPoolIfAbsent("netty-server-handler");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Request request) {
        // 引入异步业务线程池的方式，避免长时间业务耗时业务阻塞netty本身的 worker工作线程
        threadPool.execute(() -> {
            if (request.isHeartBeat()) {
                log.info("接收到客户端心跳包...");
                return;
            }
            log.info("服务器接收到请求: {}", request);
            Response response = new Response();
            response.setRequestId(request.getRequestId());
            response.setSerializationId(request.getSerializationId());
            ServiceInstance instance = localServiceProvider.getServiceProvider(request);
            if (instance == null) {
                response.setCode(1);
                log.error("服务 {} 或方法 {} 未注册", request.getService().getClazz(), request.getService().getMethod());
                response.setMessage("服务 " + request.getService().getClazz() + " 或方法 " + request.getService().getMethod() + " 未注册");
            } else {
                Object invoke = serviceInvoker.invoke(instance, request);
                if (invoke != null) {
                    response.setData(invoke);
                } else {
                    response.setCode(1);
                    response.setMessage("服务端执行方法失败");
                }
            }
            ChannelFuture future = channelHandlerContext.writeAndFlush(response);
            future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("处理过程调用时有错误发生: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("长时间未收到心跳包，断开连接...");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
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
}
