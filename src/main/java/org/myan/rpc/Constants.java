package org.myan.rpc;

/**
 * Created by myan on 2017/10/17.
 * Intellij IDEA
 */
public interface Constants {
    int ZK_SESSION_TIMEOUT = 5000;

    String ZK_REGISTRY_PATH = "/registry";
    String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";
}
