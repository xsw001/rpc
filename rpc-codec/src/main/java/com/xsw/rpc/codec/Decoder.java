package com.xsw.rpc.codec;

// 反序列化
public interface Decoder {

    static Decoder getByCode(int code) {
        switch (code) {
            case 0: {
                return new KryoDecoder();
            }
            case 1: {
                return new JSONDecoder();
            }
            default:
                return null;
        }
    }

    // 使用 泛型 省去一个再外边强制转化类 的步骤
    <T> T decode(byte[] bytes, Class<T> tClass);

    int getCode();
}
