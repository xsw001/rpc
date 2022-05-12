package com.xsw.rpc.codec;

import junit.framework.TestCase;

public class JSONDecoderTest extends TestCase {

    public void testDecode() {
        Encoder encoder = new JSONEncoder();
        TestBean bean = new TestBean("xsw", 12);

        byte[] bytes = encoder.encode(bean);

        Decoder decoder = new JSONDecoder();

        TestBean decode = decoder.decode(bytes, TestBean.class);

        System.out.println(decode);
    }
}