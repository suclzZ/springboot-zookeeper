package com.sucl.zookeeper.service.namespace;

import com.sucl.zookeeper.server.ZookeeperConnection;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author sucl
 * @date 2019/6/11
 */
@Component
public class IdSequence {
    private ZookeeperConnection connection;
    private ZkClient zkClient;
    private static final String PARENT_NODE = "/namespace";
    private static final String NODE = "/idseq";

    @Autowired
    public IdSequence(ZookeeperConnection connection){
        this.connection = connection;
        init();
    }

    private void init() {
        connection.initZkClient();
        this.zkClient = connection.getZkClient();
        if(!zkClient.exists(PARENT_NODE)){
            zkClient.createPersistent(PARENT_NODE);
        }
    }

    public String id(){
        String node = zkClient.createPersistentSequential(PARENT_NODE + NODE, null);
        return node.substring((PARENT_NODE+NODE).length());
    }

}
