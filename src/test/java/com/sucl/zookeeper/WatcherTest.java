package com.sucl.zookeeper;

import com.sucl.zookeeper.server.ZookeeperConnection;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 理解节点事件类型，与不同方法调用所触发的事件，以及对应的watcher！
 *  参考{https://blog.csdn.net/liu857279611/article/details/70495413}
 *
 * Watch一次性的触发器
 * watcher针对节点事件触发而调用
 * 节点相关事件有：
 *      EventType.NodeCreated
 *      EventType.NodeDeleted
 *      EventType.NodeDataChanged
 *      EventType.NodeChildrenChaged
 * 可以设置watcher的方法有 getChilder\ getData \ exist
 *
 * 如果获取数据时，节点数据修改，则会先触发watch事件，后接受改变的值
 *
 * @author sucl
 * @date 2019/6/3
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringbootZookeeperApplication.class)
public class WatcherTest {
    private ZooKeeper zk = ZookeeperConnection.zooKeeper;//不会初始化，参考JVM初始化条件
    private String node = "/sucl";

    @Before
    public void before(){
        zk = ZookeeperConnection.zooKeeper;
    }

//    @Test
    public void getChildrenWatch() throws KeeperException, InterruptedException {
        String path = zk.create(node, "data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        List<String> children = zk.getChildren(node, new Watcher() {
            public void process(WatchedEvent event) {
                System.out.println("getChildren: "+ event.getType());
                try {
                    zk.exists(node,this);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        System.out.println("children : " +children );

        TimeUnit.SECONDS.sleep(9999);

        zk.delete(node,-1);
    }

    @Test
    public void getDataWatch() throws InterruptedException {
        getData(node);
        TimeUnit.SECONDS.sleep(Long.MAX_VALUE);
    }

    public String getData(final String path){
        byte[] data = new byte[0];
        try {
            data = zk.getData(path, new Watcher() {
                public void process(WatchedEvent event) {
                    String value = getData(path);
                    System.out.println("value:"+value);
                }
            }, null);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("data:" + new String(data));
        return new String(data);
    }

//    @Test
    public void existWatch() throws KeeperException, InterruptedException {
        Stat e = zk.exists("/sucl1", new Watcher() {
            public void process(WatchedEvent event) {
                System.out.println("exist " + event);
            }
        });
        System.out.println("stat :" +e);

//         zk.exists("/sucl1", new Watcher() {
//            public void process(WatchedEvent event) {
//                System.out.println("exist "+event);
//            }
//        }, new AsyncCallback.StatCallback() {
//            public void processResult(int rc, String path, Object ctx, Stat stat) {
//                System.out.println("processResult "+stat);
//            }
//        },"ctx");

//        zk.delete("/sucl1",-1);
        TimeUnit.SECONDS.sleep(Long.MAX_VALUE);
    }

}
