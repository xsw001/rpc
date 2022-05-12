package com.xsw.rpc.codec;

//序列化
public interface Encoder {

    static Encoder getByCode(int code) {
        switch (code) {
            case 0: {
                return new KryoEncoder();
            }
            case 1: {
                return new JSONEncoder();
            }
            default:
                return null;
        }
    }

    byte[] encode(Object obj);

    int getCode();
}
