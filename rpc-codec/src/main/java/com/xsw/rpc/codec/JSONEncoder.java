package com.xsw.rpc.codec;

import com.alibaba.fastjson.JSON;
import com.xsw.rpc.enumeration.SerializerCode;

public class JSONEncoder implements Encoder{
    @Override
    public byte[] encode(Object obj) {
        return JSON.toJSONBytes(obj);
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("JSON").getCode();
    }
}
