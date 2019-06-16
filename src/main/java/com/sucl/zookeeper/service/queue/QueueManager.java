package com.sucl.zookeeper.service.queue;

import com.sucl.zookeeper.server.ZookeeperConnection;
import lombok.Data;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * 先进先出
 * 分布式队列
 * @author sucl
 * @date 2019/6/11
 */
@Data
@Component
public class QueueManager {
    private String PARENT_NODE = "/queue";
    private String NODE = "/msg_";
    private ZkClient zkClient;

    private ZookeeperConnection connection;

    @Autowired
    public QueueManager(ZookeeperConnection connection){
        connection.initZkClient();
        this.zkClient = connection.getZkClient();
        if(!zkClient.exists(PARENT_NODE)){
            zkClient.createPersistent(PARENT_NODE,true);
        }
    }

    public String push(Object data){
        if(data==null){
            throw new RuntimeException("data can't be null!");
        }
        return zkClient.createPersistentSequential(PARENT_NODE.concat(NODE),data);
    }

    /**
     * 并发时会出现重复值或异常,因为delete与readData非原子操作，需要在删除成功后才能返回值，处理方式
     * 1、获取子节点
     * 2、将子节点升序排序
     * 3、获取最小节点的值
     * 4、删除该节点，如果节点删除成功，返回刚获取的值
     * 5、删除失败，说明该节点不存在，获取下一个节点，重复4
     * 6、如果节点变量完还没获取到值，回到1，针对在此期间可能出现新增的节点
     * 7、在第6步可能出现死循环，需要设定策略退出
     *
     * @return
     */
    public Object pull(){
        List<String> children = zkClient.getChildren(PARENT_NODE);
        Object data = null;
        if(children!=null && children.size()>0){
            children.sort(Comparator.comparing(String::valueOf));
            for(int i=0;i<children.size();i++){
                data = zkClient.readData(PARENT_NODE+"/" + children.get(i), true);
                boolean delete = zkClient.delete(PARENT_NODE + "/" + children.get(0));
                if(data!=null && delete){
                    return data;
                }else{
                    continue;
                }
            }
            if(data ==null){
                if(!CollectionUtils.isEmpty(zkClient.getChildren(PARENT_NODE))){
                    return pull();
                }
            }
            return data;
        }
        return null;//没有数据
    }

    public void pull(Consumer c){
        while (true){
            CountDownLatch countDownLatch = new CountDownLatch(1);
            IZkChildListener listener = new IZkChildListener() {
                @Override
                public void handleChildChange(String s, List<String> list) throws Exception {
                    countDownLatch.countDown();
                }
            };
            zkClient.subscribeChildChanges(PARENT_NODE,listener);
            Object data = pull();
            if(data !=null){
                c.accept(data);
            }else{
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean exist(){
        return zkClient.getChildren(PARENT_NODE)!=null && zkClient.getChildren(PARENT_NODE).size()>0;
    }
}
