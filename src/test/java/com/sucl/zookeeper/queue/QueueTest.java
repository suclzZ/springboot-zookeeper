package com.sucl.zookeeper.queue;

import com.sucl.zookeeper.AbstractTest;
import com.sucl.zookeeper.service.queue.QueueManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * @author sucl
 * @date 2019/6/11
 */
public class QueueTest extends AbstractTest {
    @Autowired
    private QueueManager queueManager;

//    @Test
    public void test() throws InterruptedException {
        for(int i=0;i<1000;i++){
            queueManager.push("msg"+i);
        }
        TimeUnit.SECONDS.sleep(3);

        while (queueManager.exist()){
            System.out.println(queueManager.pull());
        }
    }

    @Test
    public void test2(){
        queueManager.pull(System.out::println);
    }

    @Test
    public void produce(){
        new Thread(()->{
            for(int i=0;i<1000;i++){
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                queueManager.push("msg"+i);
            }
        }).start();

    }
}
