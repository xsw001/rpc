package com.xsw.rpc.transport.http;

import com.xsw.rpc.Peer;
import com.xsw.rpc.register.ServiceRegistry;
import com.xsw.rpc.register.local.ServiceInvoker;
import com.xsw.rpc.register.local.LocalServiceProvider;
import com.xsw.rpc.register.nacos.NacosServiceRegistry;
import com.xsw.rpc.transport.RequestHandler;
import com.xsw.rpc.transport.TransportServer;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

@Slf4j
public class HTTPTransportServer implements TransportServer {
    private RequestHandler handler;
    private Server server;

    private String host;
    private int port;

    @Override
    public void init(String host, int port, RequestHandler handler) {
        this.host = host;
        this.port = port;

        this.handler = handler;
        server = new Server(new InetSocketAddress(host, port));

        // Servlet 接受请求
        ServletContextHandler ctx = new ServletContextHandler();
        server.setHandler(ctx);

        // holder 是 jetty 创建网络请求的抽象
        ServletHolder holder = new ServletHolder(new RequestServlet());
        ctx.addServlet(holder, "/*");
    }

    @Override
    public void start() {
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    class RequestServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            log.info("HTTP connect!");

            // 需要拿到client发送的数据
            InputStream in = req.getInputStream();
            // 自己处理还需要返回
            OutputStream out = resp.getOutputStream();

            if (handler != null) {
                handler.onRequest(in, out);
            }

            out.flush();
        }
    }
}
