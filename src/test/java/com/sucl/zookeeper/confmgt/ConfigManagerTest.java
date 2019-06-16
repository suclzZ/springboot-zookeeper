package com.sucl.zookeeper.confmgt;

import com.sucl.zookeeper.SpringbootZookeeperApplication;
import com.sucl.zookeeper.service.confmgt.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author sucl
 * @date 2019/6/4
 */
@SpringBootTest(classes = SpringbootZookeeperApplication.class)
@RunWith(SpringRunner.class)
public class ConfigManagerTest {
    @Autowired
    private ConfigManager configManager;
    @Autowired
    private ConfigRegistry configRegistry;

    @Test
    public void test() throws InterruptedException {
        Server s1 = new SimpleServer("server1");
        Server s2 = new SimpleServer("server2");
        Server s3 = new SimpleServer("server3");
        configRegistry.regist(s1,s2,s3);
        configManager.watchConfig();

        TimeUnit.SECONDS.sleep(Long.MAX_VALUE);
    }

//    @Test
    public void test2() throws InterruptedException {
        WatchServer s1 = new WatchServer(configManager);
        WatchServer s2 = new WatchServer(configManager);
        WatchServer s3 = new WatchServer(configManager);
        configRegistry.regist(s1,s2,s3);

        TimeUnit.SECONDS.sleep(2);
        s1.change("app:1");
        TimeUnit.SECONDS.sleep(2);
        s2.change("app:2");
        TimeUnit.SECONDS.sleep(2);
        s3.change("app:3");

        TimeUnit.SECONDS.sleep(Long.MAX_VALUE);
    }
}
