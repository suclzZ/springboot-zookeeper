package com.sucl.zookeeper.service.confmgt;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author sucl
 * @date 2019/6/4
 */
@Component
public class ConfigRegistry {
    private List<Server> servers = new ArrayList<>();

    public void regist(Server... ss){
        Arrays.stream(ss).forEach(s->{servers.add(s);});
    }

    public void run(String config){
        servers.stream().forEach(s->{
            s.refresh(config);
        });
    }
}
