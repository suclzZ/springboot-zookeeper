package com.sucl.zookeeper.service.loadbalancing.server;

import lombok.Data;

import java.io.Serializable;
import java.util.Comparator;

/**
 * server服务器节点的数据的数据模型
 * @author sucl
 * @date 2019/6/6
 */
@Data
public class ServerData implements Serializable,Comparable<ServerData> {
    //负载大小
    private Integer load;

    private String host;

    private Integer port;

    @Override
    public int compareTo(ServerData o) {
        return this.getLoad().compareTo(o.getLoad());
    }

    @Override
    public String toString() {
        return "ServerData{" +
                "load=" + load +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
