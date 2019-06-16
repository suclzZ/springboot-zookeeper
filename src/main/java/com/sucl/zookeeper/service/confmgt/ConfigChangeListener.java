package com.sucl.zookeeper.service.confmgt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author sucl
 * @date 2019/6/4
 */
@Component
public class ConfigChangeListener implements ApplicationListener<ConfigChangeEvent> {
    @Autowired
    private ConfigRegistry configRegistry;

    public void onApplicationEvent(ConfigChangeEvent event) {
        configRegistry.run(event.getConfig());
    }
}
