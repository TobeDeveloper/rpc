package org.myan.rpc.service;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.myan.rpc.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by myan on 2017/10/17.
 * Intellij IDEA
 */
public abstract class AbstractService {
    protected static final Logger LOGGER = LoggerFactory.getLogger("org.myan.rpc.service");
    protected String registryAddress;
    private CountDownLatch latch = new CountDownLatch(1);

    protected ZooKeeper connectZooKeeper() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registryAddress, Constants.ZK_SESSION_TIMEOUT, (e) -> {
                if(e.getState() == Watcher.Event.KeeperState.SyncConnected)
                    latch.countDown();
            });
            if(latch.getCount() == 0)
                latch.wait();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("Error occurs:", e);
        }
        return zk;
    }


}
