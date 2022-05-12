package com.xsw.rpc;

import com.xsw.rpc.common.annotation.Service;

@Service(name = {"minus", "add"})
public class CalcServiceImpl implements CalcService {
    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int minus(int a, int b) {
        return a - b;
    }
}
