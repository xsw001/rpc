package com.xsw.rpc;

import com.xsw.rpc.common.annotation.ServiceScan;
import com.xsw.rpc.server.RpcServer;

@ServiceScan
public class Server {
    public static void main(String[] args) {
        RpcServer server = new RpcServer();
        server.register(CalcService.class, new CalcServiceImpl());
        server.start();
    }
}
