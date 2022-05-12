package com.xsw.rpc;

import lombok.Data;

import java.io.Serializable;

// PRC的返回
@Data
public class Response  implements Serializable {
    // 服务返回码，0-成功，非0-失败
    private Integer code = 0;
    // 具体的错误原因
    private String message = "OK";
    // 返回的数据
    private Object data;
    // 相对应的 Request 的 requestId；
    private String requestId;

    private int serializationId;
}
