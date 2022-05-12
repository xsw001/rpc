package com.xsw.rpc.codec;

import junit.framework.TestCase;

public class JSONEncoderTest extends TestCase {

    public void testEncode() {
        Encoder encoder = new JSONEncoder();
        TestBean bean = new TestBean("xsw", 12);

        byte[] bytes = encoder.encode(bean);

        assertNotNull(bytes);
    }
}