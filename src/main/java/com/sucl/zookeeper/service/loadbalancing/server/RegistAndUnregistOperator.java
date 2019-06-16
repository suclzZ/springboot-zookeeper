package com.sucl.zookeeper.service.loadbalancing.server;

import lombok.Data;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

/**
 * 服务注册与注销
 * @author sucl
 * @date 2019/6/6
 */
@Data
public class RegistAndUnregistOperator {

    private String serverNodePath;

    private ZkClient zkClient;

    private ServerData serverData;


    public RegistAndUnregistOperator(String serverNodePath,ZkClient zkClient,ServerData serverData){
        this.serverNodePath =serverNodePath;
        this.zkClient = zkClient;
        this.serverData = serverData;
    }

    public void regist(){
        try {
            // 创建server对应的 临时节点
            zkClient.createEphemeral(serverNodePath, serverData);
        } catch (ZkNoNodeException e) { // 若serverNodePath的父节点不存在，那么创建持久的父节点
            String parentDir = serverNodePath.substring(0, serverNodePath.lastIndexOf('/'));
            zkClient.createPersistent(parentDir, true);
            regist();
        } catch (ZkNodeExistsException e) {
            System.out.println("该节点已存在！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregist(){
        zkClient.delete(serverNodePath);
    }
}
