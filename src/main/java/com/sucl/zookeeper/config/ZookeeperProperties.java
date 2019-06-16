package com.sucl.zookeeper.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author sucl
 * @date 2019/6/3
 */
@Data
@ConfigurationProperties(prefix = "zookeeper")
public class ZookeeperProperties {

    private String ip = "localhost";
    private List<String> connectStrs;
    private int port = 2181;
    private int timeout = 10*1000;

    public String getAddress(){
        if(getAddresss()!=null){
            return getAddresss();
        }
        return ip + ":"+port;
    }

    public String getAddresss(){
        if(connectStrs!=null){
            return StringUtils.collectionToDelimitedString(connectStrs,",");
        }
        return null;
    }
}
