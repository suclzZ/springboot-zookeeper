package com.sucl.zookeeper.service.confmgt;

/**
 * @author sucl
 * @date 2019/6/4
 */
public interface Server {

    String getName();

    void refresh(String config);
}
