package com.sucl.zookeeper.config;

import com.sucl.zookeeper.server.ZookeeperConnection;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sucl
 * @date 2019/6/3
 */
@Configuration
@EnableConfigurationProperties(ZookeeperProperties.class)
public class ZookeeperConfiguration {

    @Bean
    public ZookeeperConnection zookeeperConnection(ZookeeperProperties properties){
        ZookeeperConnection connection = new ZookeeperConnection(properties);
        return connection;
    }
}
