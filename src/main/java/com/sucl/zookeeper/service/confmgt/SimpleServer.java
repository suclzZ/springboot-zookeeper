package com.sucl.zookeeper.service.confmgt;

import java.util.UUID;

/**
 * @author sucl
 * @date 2019/6/4
 */
public class SimpleServer implements Server{
    private String name;

    public SimpleServer(String name){
        this.name = name;
    }

    public SimpleServer(){
        this.name = UUID.randomUUID().toString();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void refresh(String config) {
        System.out.println(String.format("server :%s config is changed :%s",getName(),config));
    }
}
