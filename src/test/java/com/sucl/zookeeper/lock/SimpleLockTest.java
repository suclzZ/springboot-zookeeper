package com.sucl.zookeeper.lock;

import com.sucl.zookeeper.AbstractTest;
import com.sucl.zookeeper.server.ZookeeperConnection;
import com.sucl.zookeeper.service.lock.SimpleLock;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author sucl
 * @date 2019/6/16
 */
public class SimpleLockTest extends AbstractTest {
    @Autowired
    private ZookeeperConnection connection;

    @Test
    public void test() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        Counter counter = new Counter();
        List<Thread> threads = new ArrayList<>();
        for(int i=0;i<1000;i++){
            Thread t = new Thread(()->{
//                counter.increase();
                SimpleLock lock = new SimpleLock(connection);
                boolean isLock = lock.lock();
                while (!isLock){
                    isLock = lock.lock();
                }
                add(counter);
                lock.unlock();
//                countDownLatch.countDown();
            });
            threads.add(t);
            t.start();
        }
//        countDownLatch.await();
        for(Thread t: threads){
            t.join();
        }
        System.out.println(counter.count);
    }

    private void add(Counter counter) {
        int c = counter.count;
        c++;
        counter.count = c;
    }

    class Counter{
        private int count;

        public void increase(){
//            count += 1;
            count++;
        }
    }
}
