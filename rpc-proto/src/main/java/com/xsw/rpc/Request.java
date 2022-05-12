package com.xsw.rpc;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;

// 请求
@Data
public class Request implements Serializable {
    private String requestId;
    private ServiceDescriptor service;
    private Object[] parameters;
    private boolean heartBeat;

    private int serializationId;

    @Override
    public String toString() {
        return "Request{" +
                "requestId='" + requestId + '\'' +
                ", service=" + service +
                ", parameters=" + Arrays.toString(parameters) +
                ", heartBeat=" + heartBeat +
                '}';
    }
}
