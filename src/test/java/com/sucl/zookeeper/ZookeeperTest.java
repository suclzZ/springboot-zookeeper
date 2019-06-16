package com.sucl.zookeeper;

import com.sucl.zookeeper.server.ZookeeperConnection;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 了解api提供了哪些可用的方法！
 * https://blog.csdn.net/liu857279611/article/details/70495413
 * @author sucl
 * @date 2019/6/3
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringbootZookeeperApplication.class)
public class ZookeeperTest {
    private ZooKeeper zk;
    private String node = "/sucl";

    @Before
    public void before(){
        zk = ZookeeperConnection.zooKeeper;
    }

    @Test
    public void cn() throws KeeperException, InterruptedException {
        zk.create("/ip","".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
    }

//    @Test
    public void createNode(){
        byte[] data = new String("data").getBytes();
        try {
            //同步创建 path1
            String path1 = zk.create("/path1", data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println(path1);

            //异步创建 path20000000002会在原有的路径后面加上序列号
            zk.create("/path2", data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL, new AsyncCallback.StringCallback() {
                //rc:创建状态(CodeDeprecated)，path：传入的path，ctx：，name：真实的path（path+序列号）
                public void processResult(int rc, String path, Object ctx, String name) {
                    System.out.println("StringCallback");
                }
            }, "ctx");
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * watch:
     *  true：注册defaultWatcher;false：不进行watch
     */
//    @Test
    public void getChildren(){
        try {
            List<String> data = zk.getChildren("/path1", true);
            System.out.println(data);
            zk.getChildren("/path1", new Watcher() {
                public void process(WatchedEvent event) {
                    System.out.println("watcher");
                }
            });
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    @Test
    public void getData(){
        try {
            byte[] data = zk.getData("/path1", true, null);
            System.out.println(data);

            zk.getData("/path1", new Watcher() {
                public void process(WatchedEvent event) {

                }
            }, new AsyncCallback.DataCallback() {
                public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {

                }
            },"ctx");
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setData(){
        try {
            //修改指定版本的数据，如果为-1，则修改所有版本
            zk.setData("/path1","".getBytes(),-1);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void exist(){
        try {
            Stat stat = zk.exists("/path1", new Watcher() {
                public void process(WatchedEvent event) {

                }
            });
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果有子节点，需要先删除子
     */
//    @Test
    public void delete(){
        try {
            //版本不对会出错
            zk.delete("/path1",1);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }


        //异步删除
        zk.delete("/path1", -1, new AsyncCallback.VoidCallback() {
            public void processResult(int rc, String path, Object ctx) {

            }
        },"ctx");
    }

}
