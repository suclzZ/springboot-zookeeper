package com.sucl.zookeeper.service.namespace;

import com.sucl.zookeeper.server.ZookeeperConnection;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 分布式id生成器
 * @author sucl
 * @date 2019/6/5
 */
@Component
public class IdGenerator {
    private ZookeeperConnection connection;
    private static final String PARENT_NODE = "/namespace";
    private static final String NODE = "/id";

    @Autowired
    public IdGenerator(ZookeeperConnection connection){
        this.connection = connection;
        initNode();
    }

    private void initNode() {
        try {
            if( connection.zk().exists(PARENT_NODE+NODE,false)==null){
                if(connection.zk().exists(PARENT_NODE,false)==null){
                    connection.zk().create(PARENT_NODE,null,ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
                }
                connection.zk().create(PARENT_NODE+NODE,null,ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int id(){
        try {
            Stat stat = connection.zk().setData(PARENT_NODE+NODE,null, -1);
            if(stat!=null){
                return stat.getVersion();
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
