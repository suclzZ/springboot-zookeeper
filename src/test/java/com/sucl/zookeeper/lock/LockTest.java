package com.sucl.zookeeper.lock;

import com.sucl.zookeeper.AbstractTest;
import com.sucl.zookeeper.server.ZookeeperConnection;
import com.sucl.zookeeper.service.lock.ZkLock;
import org.apache.zookeeper.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author sucl
 * @date 2019/6/16
 */
public class LockTest extends AbstractTest {
    @Autowired
    private ZookeeperConnection connection;

    @Test
    public void lock() throws InterruptedException {
        int count = 10;
        CountDownLatch countDownLatch = new CountDownLatch(count);
        List<String> result = new ArrayList<>();
        for(int i = 0; i<count; i++){
            String name = "app"+i;
            Thread app = new Thread(()->{
                ZkLock lock = new ZkLock(connection);
                lock.lock(name);//阻塞
                result.add(name);
                lock.unlock();
                countDownLatch.countDown();
            },name);
            app.start();
        }
        countDownLatch.await();
        System.out.println(result);
    }
}
