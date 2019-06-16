package com.sucl.zookeeper.namespace;

import com.sucl.zookeeper.AbstractTest;
import com.sucl.zookeeper.service.namespace.IdGenerator;
import com.sucl.zookeeper.service.namespace.IdSequence;
import com.sucl.zookeeper.service.namespace.NamespaceManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author sucl
 * @date 2019/6/5
 */
public class NamespaceTest extends AbstractTest {
    @Autowired
    private NamespaceManager namespaceManager;
    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private IdSequence idSequence;

    /**
     * 如果返回值为null，说明节点已经被使用
     */
//    @Test
    public void test(){
        String name = namespaceManager.namespace("abc");
        System.out.println(name);
    }

//    @Test
    public void test2() throws InterruptedException {
        int total = 1000;
        CountDownLatch countDownLatch = new CountDownLatch(total);
        AtomicInteger ai = new AtomicInteger(0);
        Vector<Integer> values = new Vector<>();
        for(int i=0;i<total;i++){
            new Thread(()->{
                int v = idGenerator.id();
                values.add(v);
                System.out.println("index: "+ ai.incrementAndGet()+"   value:"+ v);
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        values.sort(Comparator.comparing(Integer::intValue).reversed());
        System.out.println(ai.get());
        System.out.println(values.get(0));
    }

    @Test
    public void test3() throws InterruptedException {
        int total = 1000;
        CountDownLatch countDownLatch = new CountDownLatch(total);
        Vector<String> values = new Vector<>();
        for(int i=0;i<total;i++){
            new Thread(()->{
                String v = idSequence.id();
                values.add(v);
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        Collections.sort(values, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.compareTo(o1);
            }
        });
        System.out.println(values.get(0));
    }
}
