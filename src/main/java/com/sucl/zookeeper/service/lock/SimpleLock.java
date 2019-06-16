package com.sucl.zookeeper.service.lock;

import com.sucl.zookeeper.server.ZookeeperConnection;
import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * 因为zk节点的唯一性，我们可以定义一个/slock节点作为锁
 * 不可重入锁
 * @author sucl
 * @date 2019/6/16
 */
public class SimpleLock implements Watcher{
    private static final String LOCK_NODE = "/slock";
    private ZooKeeper zk;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public SimpleLock(ZookeeperConnection connection){
        this.zk = connection.zk();
    }

    public boolean lock(){
        try {
            if(zk.exists(LOCK_NODE, this)!=null){//锁存在，则等待，可以设置超时时间
                countDownLatch.await();
            }
            zk.create(LOCK_NODE,"".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
            System.out.println("创建锁");
            return true;
        } catch (KeeperException e) {
            if(e instanceof KeeperException.NodeExistsException){//竞争时其他节点创建成功
                return false;
            }
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void process(WatchedEvent event) {
        if(event.getType() == Event.EventType.NodeDeleted){
            countDownLatch.countDown();
        }
    }

    public void unlock(){
        try {
            System.out.println("释放锁");
            zk.delete(LOCK_NODE,-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
}
