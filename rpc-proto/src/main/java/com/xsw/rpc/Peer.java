package com.xsw.rpc;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 表示网络传输的一个端点
 */
@Data
@AllArgsConstructor // 所有字段的注解
public class Peer implements Serializable {
    private String host;
    private Integer port;
}
