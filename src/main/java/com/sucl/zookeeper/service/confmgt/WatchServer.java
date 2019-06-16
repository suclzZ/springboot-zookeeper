package com.sucl.zookeeper.service.confmgt;

import jdk.nashorn.internal.runtime.regexp.joni.constants.CCSTATE;

/**
 * @author sucl
 * @date 2019/6/4
 */
public class WatchServer extends SimpleServer{
    private ConfigManager configManager;

    public WatchServer(ConfigManager configManager){
        this.configManager = configManager;
        configManager.watchConfig();
    }

    public void change(String config){
        configManager.setData(config);
    }
}
