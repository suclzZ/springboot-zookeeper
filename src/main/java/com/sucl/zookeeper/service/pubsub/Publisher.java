package com.sucl.zookeeper.service.pubsub;

import lombok.Data;
import org.I0Itec.zkclient.ZkClient;


/**
 * @author sucl
 * @date 2019/6/12
 */
@Data
public class Publisher {
    private static String NODE = "/info";
    private String channel;
    private ZkClient zkClient;

    public Publisher(String channel,ZkClient zkClient){
        this.channel = channel;
        this.zkClient = zkClient;
        if(!zkClient.exists("/".concat(channel))){
            zkClient.createPersistent("/".concat(channel));
        }
    }

    public void push(Object data) {
        zkClient.createPersistentSequential("/".concat(channel).concat(NODE),data);
    }

    public void subscribe(Subscibe subscibe) {
        zkClient.subscribeChildChanges("/".concat(channel),subscibe);
        zkClient.subscribeDataChanges("/".concat(channel),subscibe);
    }

    public void unsubscribe(Subscibe subscibe){
        zkClient.unsubscribeDataChanges("/".concat(channel),subscibe);
        zkClient.unsubscribeChildChanges("/".concat(channel),subscibe);
    }
}
