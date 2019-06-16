package com.sucl.zookeeper.pubsub;

import com.sucl.zookeeper.AbstractTest;
import com.sucl.zookeeper.service.pubsub.PSCenterManager;
import com.sucl.zookeeper.service.pubsub.Publisher;
import com.sucl.zookeeper.service.pubsub.Subscibe;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * @author sucl
 * @date 2019/6/12
 */
public class PubSubTest extends AbstractTest {
    @Autowired
    private PSCenterManager centerManager;

    @Test
    public void test() throws InterruptedException {
        String channel = "channel";
        ZkClient zkClient = centerManager.getZkClient();
        zkClient.setZkSerializer(new ZkSerializer(){

            @Override
            public byte[] serialize(Object data) throws ZkMarshallingError {
                return data==null?null:data.toString().getBytes();
            }

            @Override
            public Object deserialize(byte[] bytes) throws ZkMarshallingError {
                return new String(bytes);
            }
        });//

        Publisher publisher = new Publisher(channel,zkClient);
        Subscibe subscibe = new Subscibe(channel,zkClient);

        subscibe.setCallback(new Subscibe.Callback() {
            @Override
            public void call(Object data) {
                System.out.println("数据:"+data);
            }
        });

        centerManager.addPublisher(publisher);

        centerManager.registry(subscibe);

        for(int i=0;i<10;i++){
            centerManager.push("data"+i);
            TimeUnit.SECONDS.sleep(1);
        }

        while (true){

        }
    }
}
