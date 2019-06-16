package com.sucl.zookeeper.service.pubsub;

import lombok.Data;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.Comparator;
import java.util.List;

/**
 * 订阅对应节点，关注其节点（包括子节点）以及数据变化
 * @author sucl
 * @date 2019/6/12
 */
@Data
public class Subscibe implements IZkChildListener ,IZkDataListener {
    private String channel;
    private ZkClient zkClient;
    private Callback callback;

    public Subscibe(String channel,ZkClient zkClient){
        this.channel = channel;
        this.zkClient = zkClient;
    }

    @Override
    public void handleChildChange(String node, List<String> list) throws Exception {
        List<String> children = zkClient.getChildren("/".concat(channel));
        if(children!=null && children.size()>0){
            children.sort(Comparator.comparing(String::valueOf));
            for(String child : children){
                Object data = zkClient.readData("/".concat(channel).concat("/").concat(child));
                if(callback!=null){
                    callback.call(data);
                }
                zkClient.delete("/".concat(channel).concat("/").concat(child));
            }
        }else{

        }

        zkClient.subscribeChildChanges("/".concat(channel),this);
    }

    @Override
    public void handleDataChange(String dataPath, Object data) throws Exception {
        System.out.println("数据修改: "+ dataPath +":" +data);
    }

    @Override
    public void handleDataDeleted(String dataPath) throws Exception {
        System.out.println("节点数据删除："+dataPath);
    }

    public interface Callback{
        void call(Object data);
    }
}
