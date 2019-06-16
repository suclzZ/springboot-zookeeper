package com.sucl.zookeeper.service.pubsub;

import com.sucl.zookeeper.server.ZookeeperConnection;
import lombok.Data;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author sucl
 * @date 2019/6/12
 */
@Data
@Component
public class PSCenterManager {
    private List<Publisher> publishers = new ArrayList<>();
    private ZkClient zkClient;

    @Autowired
    public PSCenterManager(ZookeeperConnection connection){
        this.zkClient = connection.getZkClient();
    }

    public void addPublisher(Publisher ... _publishers){
        publishers.addAll(Arrays.asList(_publishers));
    }

    /**
     * 发布消息
     * @param data
     */
    public void push(Object data){
        for(Publisher publisher : publishers){
            publisher.push(data);
        }
    }

    /**
     * 订阅节点
     * @param subscibe
     */
    public void registry(Subscibe subscibe){
        for(Publisher publisher : publishers){
            if(subscibe.getChannel().equals(publisher.getChannel())){
                publisher.subscribe(subscibe);
            }
        }
    }

    public void unregistry(Subscibe subscibe){
        for(Publisher publisher : publishers){
            if(subscibe.getChannel().equals(publisher.getChannel())){
                publisher.unsubscribe(subscibe);
            }
        }
    }

}
