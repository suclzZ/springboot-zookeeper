package com.sucl.zookeeper.service.loadbalancing.client;

import com.sucl.zookeeper.service.loadbalancing.server.ServerData;
import lombok.Data;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

import javax.naming.Name;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author sucl
 * @date 2019/6/6
 */
@Data
public class LoadBalanceImplProvider<T extends ServerData> {
    /** ip 端口 */
    private String ipAndPort;

    /** /servers节点路径 */
    private String serversNodePath;

    /** ZkClient客户端 */
    private ZkClient zkClient;

    /** 会话超时时间 */
    private static final Integer SESSION_TIME_OUT = 10000;

    /** 连接超时时间 */
    private static final Integer CONNECT_TIME_OUT = 10000;

    /** 节点路径分隔符 */
    private static final String NODE_SEPARATOR = "/";

    public LoadBalanceImplProvider(String ipAndPort, String serversNodePath) {
        this.serversNodePath = serversNodePath;
        this.ipAndPort = ipAndPort;
        this.zkClient = new ZkClient(this.ipAndPort, SESSION_TIME_OUT, CONNECT_TIME_OUT, new SerializableSerializer());
    }

    public List<ServerData> getBalanceItems() {
        List<ServerData> serverDataList = new ArrayList<>();
        List<String> children = zkClient.getChildren(serversNodePath);
        if(children!=null && children.size()>0){
            for(int i=0;i<children.size();i++){
                ServerData serverData = zkClient.readData(serversNodePath +NODE_SEPARATOR+ children.get(i));
                serverDataList.add(serverData);
            }
        }
        return serverDataList;
    }

    public ServerData balanceAlgorithm(List<ServerData> items) {
        if(items!=null && items.size()>0){
            Collections.sort(items);
            return items.get(0);
        }else{
            return null;
        }
    }
}
