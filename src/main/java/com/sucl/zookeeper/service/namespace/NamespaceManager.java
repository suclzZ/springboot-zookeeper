package com.sucl.zookeeper.service.namespace;

import com.sucl.zookeeper.server.ZookeeperConnection;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 唯一命名
 * @author sucl
 * @date 2019/6/5
 */
@Component
public class NamespaceManager {
    private static final String NODE = "/namespace";
    private boolean enable;
    private ZookeeperConnection connection;

    @Autowired
    public NamespaceManager(ZookeeperConnection connection){
        this.connection = connection;
        initNode();
    }

    private void initNode() {
        String parent = null;
        try {
            Stat stat = connection.zk().exists(NODE, true);
            if(stat==null){
                parent = connection.zk().create(NODE, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }else{
                enable = true;
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(parent==null && !enable){
            throw new RuntimeException(String.format("create parent node '%s' failure!",NODE));
        }
        enable = true;
    }

    public String namespace(String namespace){
        if(enable){
            String name = null;
            try {
                name = connection.zk().create(NODE+"/"+namespace,null,ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(name ==null){
                return null;
            }else{
                return name.substring(NODE.length()+1);
            }
        }else{
            throw new RuntimeException(String.format("namespace manager is disabled"));
        }
    }
}
