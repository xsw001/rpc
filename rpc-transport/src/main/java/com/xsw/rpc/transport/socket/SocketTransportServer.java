package com.xsw.rpc.transport.socket;

import com.xsw.rpc.Peer;
import com.xsw.rpc.Request;
import com.xsw.rpc.Response;
import com.xsw.rpc.common.utils.ReflectionUtils;
import com.xsw.rpc.common.utils.ThreadPoolFactoryUtil;
import com.xsw.rpc.register.ServiceRegistry;
import com.xsw.rpc.register.local.ServiceInstance;
import com.xsw.rpc.register.local.ServiceInvoker;
import com.xsw.rpc.register.local.LocalServiceProvider;
import com.xsw.rpc.register.nacos.NacosServiceRegistry;
import com.xsw.rpc.transport.RequestHandler;
import com.xsw.rpc.transport.TransportServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

@Slf4j
public class SocketTransportServer implements TransportServer {

    private final ExecutorService threadPool;
    private String host;
    private int port;

    public SocketTransportServer() {
        threadPool = ThreadPoolFactoryUtil.createCustomThreadPoolIfAbsent("Socket-server");
    }

    @Override
    public void init(String host, int port, RequestHandler handler) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void start() {
        try (ServerSocket server = new ServerSocket()) {
            server.bind(new InetSocketAddress(host, port));
            Socket socket;
            while ((socket = server.accept()) != null) {
                log.info("客户端: {} 已连接", socket.getInetAddress());
                threadPool.execute(new SocketRpcRequestHandler(socket));
            }
            threadPool.shutdown();
        } catch (Exception e) {
            log.error("SocketTransportServer IOException:", e);
        }
    }

    @Override
    public void stop() {

    }

    static class SocketRpcRequestHandler implements Runnable {

        private final Socket socket;
        private final LocalServiceProvider localServiceProvider;
        private final ServiceInvoker serviceInvoker;


        public SocketRpcRequestHandler(Socket socket) {
            this.socket = socket;
            serviceInvoker = ReflectionUtils.newInstance(ServiceInvoker.class);
            localServiceProvider = new LocalServiceProvider();
        }

        @Override
        public void run() {
            Response response = new Response();
            try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
                //3.通过输入流读取客户端发送的请求信息
                Request request = (Request) objectInputStream.readObject();
                Object invoke = null;
                try {
                    ServiceInstance instance = localServiceProvider.getServiceProvider(request);
                    invoke = serviceInvoker.invoke(instance, request);
                } catch (Exception e) {
                    log.error("服务端执行方法失败");
                } finally {
                    //4.通过输出流向客户端发送响应信息
                    if (invoke == null) {
                        response.setCode(1);
                    }
                    response.setData(invoke);
                    objectOutputStream.writeObject(response);
                    objectOutputStream.flush();
                }

            } catch (IOException | ClassNotFoundException e) {
                log.error("SocketTransportServer 接受失败:", e);
            }

        }
    }
}
