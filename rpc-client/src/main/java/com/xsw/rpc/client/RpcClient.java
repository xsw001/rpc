package com.xsw.rpc.client;

import com.xsw.rpc.Request;
import com.xsw.rpc.Response;
import com.xsw.rpc.ServiceDescriptor;
import com.xsw.rpc.codec.Decoder;
import com.xsw.rpc.codec.Encoder;
import com.xsw.rpc.common.utils.ReflectionUtils;
import com.xsw.rpc.transport.TransportClient;
import lombok.extern.slf4j.Slf4j;
import sun.misc.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

@Slf4j
public class RpcClient implements InvocationHandler {

    private RpcClientConfig config;
    private Encoder encoder;
    private Decoder decoder;
//    private TransportSelector selector;

    private Class<?> clazz;


    public RpcClient() {
        this(new RpcClientConfig());
    }

    public RpcClient(RpcClientConfig config) {
        this.config = config;

        encoder = ReflectionUtils.newInstance(config.getEncoderClass());
        decoder = ReflectionUtils.newInstance(config.getDecoderClass());

//        selector = ReflectionUtils.newInstance(config.getSelectorClass());
//        selector.init(config.getServers(), config.getConnection(), config.getTransportClientClass());
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        this.clazz = clazz;
        return (T) Proxy.newProxyInstance(
                getClass().getClassLoader(),//当前class的ClassLoader
                new Class<?>[]{clazz},
                this);
    }

    @Override
    public String toString() {
        return "RpcClient{" +
                "config=" + config +
                ", encoder=" + encoder +
                ", decoder=" + decoder +
                '}';
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Request request = new Request();
        request.setService(ServiceDescriptor.from(clazz, method));
        request.setParameters(args);
        request.setHeartBeat(false);
        request.setRequestId(UUID.randomUUID().toString());
        request.setSerializationId(encoder.getCode());

        // 网络通信，调取服务, 获取响应
        Response response = remoteInvoke(request);
        if (response == null || response.getCode() != 0) {
            throw new IllegalStateException("远程调用失败------>" + response.getMessage());
        }
        return response.getData();
    }

    private Response remoteInvoke(Request request) {
        Response response;
        TransportClient client = ReflectionUtils.newInstance(this.config.getTransportClientClass());
        try {
            response = (Response) client.write(request);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            response = new Response();
            response.setCode(1);
            response.setMessage("RPCClient 出错" + e.getClass()
                    + ":" + e.getMessage());
        }
        return response;
    }
}
