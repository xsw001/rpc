package com.xsw.rpc.server;

import com.xsw.rpc.Peer;
import com.xsw.rpc.Request;
import com.xsw.rpc.Response;
import com.xsw.rpc.codec.Decoder;
import com.xsw.rpc.codec.Encoder;
import com.xsw.rpc.common.utils.ReflectionUtils;
import com.xsw.rpc.register.AutoRegistration;
import com.xsw.rpc.register.ServiceRegistry;
import com.xsw.rpc.register.local.ServiceInstance;
import com.xsw.rpc.register.local.ServiceInvoker;
import com.xsw.rpc.register.local.LocalServiceProvider;
import com.xsw.rpc.transport.RequestHandler;
import com.xsw.rpc.transport.TransportServer;
import lombok.extern.slf4j.Slf4j;
import sun.misc.IOUtils;

import java.io.*;

@Slf4j
public class RpcServer {
    // 配置信息
    private RpcServerConfig config;
    // 网络模块
    private TransportServer net;
    // 序列化
    private Encoder encoder;
    private Decoder decoder;
    // 调用具体服务
    private ServiceInvoker serviceInvoker;

    private AutoRegistration autoRegistration;

    private LocalServiceProvider localServiceProvider;

    private RequestHandler handler = new HttpRequestHandler();

    public RpcServer() {
        this(new RpcServerConfig());
    }

    public RpcServer(RpcServerConfig config) {
        this.config = config;

        // net
        this.net = ReflectionUtils.newInstance(config.getTransportClass());
        net.init(config.getHost(), config.getPort(), this.handler);

        this.encoder = ReflectionUtils.newInstance(config.getEncoder());
        this.decoder = ReflectionUtils.newInstance(config.getDecoder());

        this.serviceInvoker = new ServiceInvoker();
        this.autoRegistration = new AutoRegistration(config.getHost(), config.getPort());

        this.localServiceProvider = new LocalServiceProvider();
    }

    public <T> void register(Class<T> interfaceClass, T bean) {
        autoRegistration.scanServices();
    }

    public void start() {
        net.start();
    }

    public void stop() {
        net.stop();
    }


    class HttpRequestHandler implements RequestHandler {
        @Override
        public void onRequest(InputStream recive, OutputStream toResponse) {
            Response response = new Response();
            try {
                byte[] bytes = IOUtils.readFully(recive, recive.available(), true);
                Request request = decoder.decode(bytes, Request.class);
                log.info("get request: {}", request);

                // 找服务
                ServiceInstance sis = localServiceProvider.getServiceProvider(request);
                // 调用
                Object invoke = serviceInvoker.invoke(sis, request);
                response.setData(invoke);


            } catch (Exception e) {
                log.warn(e.getMessage(), e);
                response.setCode(1);
                response.setMessage("RpcServer got error: " + e.getClass().getName()
                        + ":" + e.getMessage());
            } finally {
                byte[] bytes = encoder.encode(response);
                try {
                    toResponse.write(bytes);
                    log.info("response client");
                } catch (IOException e) {
                    log.warn(e.getMessage(), e);
                }
            }
        }
    }

}
