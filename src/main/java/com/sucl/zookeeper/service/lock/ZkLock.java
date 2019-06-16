package com.sucl.zookeeper.service.lock;

import com.sucl.zookeeper.server.ZookeeperConnection;
import org.apache.zookeeper.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.sql.Time;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 1、建立/lock节点，将所有锁节点创建在其下面
 * 2、有系统获取锁，在/lock节点下创建有序的临时节点/zl
 * 3、其它系统获取锁的过程：
 *      a.获取/lock所有子节点，并按升序排序；
 *      b.判断自己是否是最小节点，如果是则取锁成功；
 *      c.否则监听/lock节点的EventType.NodeChildrenChanged,一段触发，则重新进行a步骤(羊群效应，改为监听该节点上一节点)
 *      d.
 *
 * @author sucl
 * @date 2019/6/16
 */
public class ZkLock {
    private ZooKeeper zk;
    private String PARENT_NODE = "/lock";
    private String NODE = "/zl";
    private String lockPath;

    public ZkLock(ZookeeperConnection connection){
        this.zk = connection.zk();
        init();
    }

    private void init() {
        try {
            if(zk.exists(PARENT_NODE,true)==null){
                zk.create(PARENT_NODE,"lock".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void lock(String id){
        try {
            this.lockPath = zk.create(PARENT_NODE.concat(NODE), id.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

            List<String> children = zk.getChildren(PARENT_NODE, true);
            if(!CollectionUtils.isEmpty(children)){
                children.sort(Comparator.comparing(String::valueOf));
                if(!PARENT_NODE.concat("/").concat(children.get(0)).equals(lockPath)){
                    int preIndex = Collections.binarySearch(children,lockPath.substring(PARENT_NODE.length()+1));
                    System.out.println("id:"+id+" 锁节点："+lockPath);
                    CountDownLatch countDownLatch = new CountDownLatch(1);
                    zk.exists(PARENT_NODE.concat("/").concat(children.get(preIndex-1)), new Watcher() {
                        @Override
                        public void process(WatchedEvent event) {
                            if(event.getType() == Event.EventType.NodeDeleted){
                                countDownLatch.countDown();
//                                System.out.println("删除锁节点："+event.getPath());
                            }
                        }
                    });
                    countDownLatch.await();
                }
                System.out.println("获取锁："+lockPath+" id:"+id);
            }

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void unlock(){
        if(!StringUtils.isEmpty(lockPath)){
            try {
                System.out.println("释放锁:"+lockPath);
                zk.delete(lockPath,-1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (KeeperException e) {
                e.printStackTrace();
            }
        }
    }
}
