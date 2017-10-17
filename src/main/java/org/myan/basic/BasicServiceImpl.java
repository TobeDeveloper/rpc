package org.myan.basic;

import org.myan.rpc.annotation.RpcService;

/**
 * Created by myan on 2017/10/17.
 * Intellij IDEA
 */
@RpcService(BasicService.class)
public class BasicServiceImpl implements BasicService{
    @Override
    public String welcome(String name) {
        return "Welcome " + name;
    }
}
