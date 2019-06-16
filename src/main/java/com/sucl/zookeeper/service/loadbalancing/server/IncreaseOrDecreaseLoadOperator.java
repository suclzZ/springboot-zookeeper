package com.sucl.zookeeper.service.loadbalancing.server;

import lombok.Data;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.data.Stat;

/**
 * 对节点的负载量进行增加/减少的封装类
 * @author sucl
 * @date 2019/6/6
 */
@Data
public class IncreaseOrDecreaseLoadOperator {

    //服务节点
    private String serverNodePath;

    private ZkClient zkClient;

    public IncreaseOrDecreaseLoadOperator(String serverNodePath,ZkClient zkClient){
        this.serverNodePath = serverNodePath;
        this.zkClient = zkClient;
    }

    /**
     * 增加
     * @param step
     * @return
     */
    public boolean increaseLoad(Integer step){
        Stat stat = new Stat();
        while (true){
            ServerData  serverData  = zkClient.readData(serverNodePath, stat);
            serverData.setLoad(serverData.getLoad()+step);
            zkClient.writeData(serverNodePath,serverData,stat.getVersion());
            return true;
        }
    }

    /**
     * 减少
     * @param step
     * @return
     */
    public boolean decreaseLoad(Integer step){
        Stat stat = new Stat();
        while (true){
            ServerData serverData = zkClient.readData(serverNodePath, stat);
            Integer currentLoad = serverData.getLoad();
            serverData.setLoad(currentLoad>step?currentLoad-step:0);
            zkClient.writeData(serverNodePath,serverData);
            return true;
        }
    }
}
