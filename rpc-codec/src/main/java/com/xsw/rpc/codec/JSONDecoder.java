package com.xsw.rpc.codec;

import com.alibaba.fastjson.JSON;
import com.xsw.rpc.enumeration.SerializerCode;

public class JSONDecoder implements Decoder {
    @Override
    public <T> T decode(byte[] bytes, Class<T> tClass) {
        return JSON.parseObject(bytes, tClass);
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("JSON").getCode();
    }
}
