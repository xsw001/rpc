package com.xsw.rpc.register.nacos.loadBalance;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

public class RoundRobinLoadBalancer implements LoadBalancer {

    private int idx = 0;

    @Override
    public Instance select(List<Instance> instances) {
        Instance instance = instances.get(idx);
        idx = (idx + 1) % instances.size();
        return instance;
    }
}
