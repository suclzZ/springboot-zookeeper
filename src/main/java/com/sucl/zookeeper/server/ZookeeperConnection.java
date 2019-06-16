package com.sucl.zookeeper.server;

import com.sucl.zookeeper.config.ZookeeperProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.server.ZooKeeperServer;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static org.apache.zookeeper.ZooKeeper.States.CONNECTED;

/**
 * @author sucl
 * @date 2019/6/3
 */
@Data
@NoArgsConstructor
public class ZookeeperConnection implements Watcher {
    private ZookeeperProperties properties;
    public static ZooKeeper zooKeeper;
    private ZkClient zkClient;
    private CuratorFramework curatorFramework;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public ZookeeperConnection(ZookeeperProperties properties){
        this.properties = properties;
    }

    public ZooKeeper zk(){
        if(zooKeeper==null){
            connect();
        }
        if(zooKeeper==null){
            throw new RuntimeException("create connection failure!");
        }
        return zooKeeper;
    }

    @PostConstruct
    public void init(){
        connect();
    }

    public void initZkClient(){
        this.zkClient = new ZkClient(properties.getAddress(),properties.getTimeout(),properties.getTimeout(),new SerializableSerializer());
    }

    public void initCurator(){
        RetryPolicy retyrPolicy = new RetryNTimes(3000,1000);
        this.curatorFramework = CuratorFrameworkFactory.newClient(properties.getAddress(), properties.getTimeout(), properties.getTimeout(), retyrPolicy);
    }

    public void connect(){
        try {
            zooKeeper = new ZooKeeper(properties.getAddress(),properties.getTimeout(),this);
            countDownLatch.await();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("zookeeper connected!");
    }

    public void process(WatchedEvent event) {
        if(event.getState() == Event.KeeperState.SyncConnected){
            countDownLatch.countDown();
        }
    }
}
