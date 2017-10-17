package org.myan.rpc.service;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.myan.rpc.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by myan on 2017/10/17.
 * Intellij IDEA
 */
public class ServiceDiscovery extends AbstractService {

    private volatile List<String> dataList = new ArrayList<>();

    public ServiceDiscovery(String registryAddress) {
        this.registryAddress = registryAddress;

        ZooKeeper zk = connectZooKeeper();
        if (zk != null) {
            watchNode(zk);
        }
    }

    private void watchNode(final ZooKeeper zk) {
        try {
            List<String> nodes = zk.getChildren(Constants.ZK_REGISTRY_PATH, e -> {
                if(e.getType() == Watcher.Event.EventType.NodeChildrenChanged)
                    watchNode(zk);
            });

            List<String> data = new ArrayList<>();
            for (String node : nodes) {
                byte[] bytes = zk.getData(Constants.ZK_REGISTRY_PATH + "/" + node, false, null);
                data.add(new String(bytes));
            }
            LOGGER.debug("node data: {}", data);
            this.dataList = data;
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String discover() {
        String data = null;
        int size = dataList.size();
        if (size > 0) {
            if (size == 1) {
                data = dataList.get(0);
                LOGGER.debug("using only data: {}", data);
            } else {
                data = dataList.get(ThreadLocalRandom.current().nextInt(size));
                LOGGER.debug("using random data: {}", data);
            }
        }
        return data;
    }
}
