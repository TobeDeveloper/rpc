package service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.myan.basic.BasicService;
import org.myan.rpc.client.RpcProxy;
import org.myan.rpc.service.ServiceDiscovery;

/**
 * Created by myan on 2017/10/17.
 * Intellij IDEA
 */
public class BasicServiceTestCase {
    private RpcProxy proxy;

    @Before
    public void setUp(){
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery("127.0.0.1:2181");
        proxy = new RpcProxy(serviceDiscovery);
    }

    @Test
    public void testService() {
        BasicService service = proxy.create(BasicService.class);
        String result = service.welcome("myan");
        Assert.assertEquals("Welcome myan", result);
    }

}
