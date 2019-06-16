package com.sucl.zookeeper.lock;

import com.sucl.zookeeper.AbstractTest;
import com.sucl.zookeeper.server.ZookeeperConnection;
import com.sucl.zookeeper.service.lock.ZkLock;
import org.apache.zookeeper.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author sucl
 * @date 2019/6/16
 */
public class LockWatchTest extends AbstractTest {
    @Autowired
    private ZookeeperConnection connection;

    @Test
    public void test() throws KeeperException, InterruptedException {
        ZooKeeper zk = connection.zk();
        CountDownLatch cdl = new CountDownLatch(1);
        if(zk.exists("/lock", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                cdl.countDown();
            }
        })==null){
            zk.create("/lock","".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
        }
        cdl.await();
        lockWatch(connection.zk());
        TimeUnit.SECONDS.sleep(Long.MAX_VALUE);
    }

    private void lockWatch(ZooKeeper zk) throws KeeperException, InterruptedException {
        List<String> children = zk.getChildren("/lock", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getType() == Event.EventType.NodeChildrenChanged) {
                    try {
//                        System.out.println(zk.getChildren("/lock", false));
                        lockWatch(zk);
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        children.sort(Comparator.comparing(String::valueOf));
        System.out.println(children);
    }
}
