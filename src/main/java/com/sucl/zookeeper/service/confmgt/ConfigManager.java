package com.sucl.zookeeper.service.confmgt;

import com.sucl.zookeeper.server.ZookeeperConnection;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * 配置管理
 * 1、统一监听，一段发现配置修改则通知各个子服务
 * 2、各个服务统一监听节点，有任何一个服务对配置修改，其他配置则收到
 * @author sucl
 * @date 2019/6/4
 */
@Component
public class ConfigManager implements Watcher,ApplicationEventPublisherAware {
    private String configNode = "/config";
    @Autowired
    private ZookeeperConnection connection;

    private ApplicationEventPublisher applicationEventPublisher;

    public String watchConfig(){
        try {
            byte[] data = connection.zk().getData(configNode, this, null);
            return new String(data);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setData(String config){
        try {
            connection.zk().setData(configNode,config.getBytes(),-1);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void process(WatchedEvent event) {
        String config = watchConfig();//持续监听
        applicationEventPublisher.publishEvent(new ConfigChangeEvent(config));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
