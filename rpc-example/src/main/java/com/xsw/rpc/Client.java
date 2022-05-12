package com.xsw.rpc;

import com.xsw.rpc.client.RpcClient;


public class Client {
    public static void main(String[] args) {
        RpcClient client = new RpcClient();

        CalcService proxy = client.getProxy(CalcService.class);
        System.out.println(proxy.minus(8, 9));
        System.out.println(proxy.add(1, 3));
    }
}
