package org.myan.rpc.client;

import org.myan.rpc.core.RpcRequest;
import org.myan.rpc.core.RpcResponse;
import org.myan.rpc.service.ServiceDiscovery;

import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * Created by myan on 2017/10/17.
 * Intellij IDEA
 */
public class RpcProxy {
    private String serverAddress;
    private ServiceDiscovery serviceDiscovery;

    public RpcProxy(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> interfaceClass) {
        return (T)Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                (obj, method, args) -> {
                    RpcRequest request = new RpcRequest();
                    request.setRequestId(UUID.randomUUID().toString());
                    request.setClassName(method.getDeclaringClass().getName());
                    request.setMethodName(method.getName());
                    request.setParameterTypes(method.getParameterTypes());
                    request.setParameters(args);

                    // discover service from registry
                    if(serviceDiscovery != null)
                        serverAddress = serviceDiscovery.discover();
                    String[] array = serverAddress.split(":");
                    if(array.length != 2)
                        return null;

                    // set up client
                    RpcClient client = new RpcClient(array[0], Integer.parseInt(array[1]));
                    RpcResponse response = client.send(request);
                    if (response.isError()) {
                        throw response.getError();
                    } else {
                        return response.getResult();
                    }
                }
        );
    }
}
