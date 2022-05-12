package com.xsw.rpc.transport.http;

import com.xsw.rpc.Peer;
import com.xsw.rpc.Request;
import com.xsw.rpc.Response;
import com.xsw.rpc.codec.Decoder;
import com.xsw.rpc.codec.Encoder;
import com.xsw.rpc.codec.KryoDecoder;
import com.xsw.rpc.codec.KryoEncoder;
import com.xsw.rpc.common.utils.ReflectionUtils;
import com.xsw.rpc.register.ServiceRegistry;
import com.xsw.rpc.register.nacos.NacosServiceRegistry;
import com.xsw.rpc.transport.TransportClient;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

public class HTTPTransportClient implements TransportClient {

    private String url;
    private final Encoder encoder;
    private final Decoder decoder;
    private final ServiceRegistry serviceRegistry;

    public HTTPTransportClient() {
        encoder = ReflectionUtils.newInstance(KryoEncoder.class);
        decoder = ReflectionUtils.newInstance(KryoDecoder.class);
        serviceRegistry = new NacosServiceRegistry();
    }

    @Override
    public void connect(Peer peer) {
        url = "http://" + peer.getHost() + ":" + peer.getPort();
    }

    @Override
    public Object write(Request request) {
        try {
            Peer peer = serviceRegistry.lookup(request.getService().getClazz());
            url = "http://" + peer.getHost() + ":" + peer.getPort();
            HttpURLConnection http = (HttpURLConnection) new URL(url).openConnection();
            http.setDoOutput(true);
            http.setDoInput(true);
            http.setRequestMethod("POST");
            http.setUseCaches(false);
            // 连接
            http.connect();
            // 发送数据
            byte[] data = encoder.encode(request);
            IOUtils.copy(new ByteArrayInputStream(data), http.getOutputStream());
            int code = http.getResponseCode();
            InputStream recive;
            if (code == HttpURLConnection.HTTP_OK)
                recive = http.getInputStream();
            else
                recive = http.getErrorStream();
            byte[] inBytes = sun.misc.IOUtils.readFully(recive, recive.available(), true);
            return decoder.decode(inBytes, Response.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() {

    }
}
