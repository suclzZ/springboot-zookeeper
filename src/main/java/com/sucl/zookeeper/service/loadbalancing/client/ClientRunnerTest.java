package com.sucl.zookeeper.service.loadbalancing.client;

import com.sucl.zookeeper.service.loadbalancing.server.ServerData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sucl
 * @date 2019/6/6
 */
public class ClientRunnerTest {

    private static final int SIZE = 5;

    /** 连接zookeeper的ip和端口 */
    private static final String IP_PORT = "localhost:2181";

    /** /servers节点路径 */
    private static final String SERVERS_PATH = "/servers";

    /**
     * 程序入口
     */
    public static void main(String[] args) {
        List<Thread> threadList = new ArrayList<>(SIZE);
        final List<ClientLogicImpl> clientList = new ArrayList<>(8);
        // 负载均衡实现 的封装类
        final LoadBalanceImplProvider<ServerData> balanceProvider = new LoadBalanceImplProvider<>(IP_PORT, SERVERS_PATH);
        try {
            for (int i = 0; i < SIZE; i++) {

                Thread thread = new Thread(new Runnable() {

                    public void run() {
                        ClientLogicImpl clientLogicImpl = new ClientLogicImpl(balanceProvider);
                        clientList.add(clientLogicImpl);
                        try {
                            clientLogicImpl.connect();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                threadList.add(thread);
                thread.start();
                //延时
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭客户端
            for (ClientLogicImpl clientLogicImpl : clientList) {
                try {
                    clientLogicImpl.disConnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //关闭线程
            for (Thread thread : threadList) {
                // 将线程置为打断状态
                thread.interrupt();
                try {
                    // 若果线程处于打断状态，那么当其 调用wait()、join()、sleep()方法时，会抛出InterruptedException异常
                    thread.join();
                    System.out.println("线程" + thread.getName() + "关闭了！");
                } catch (InterruptedException e) {
                    System.out.println("线程" + thread.getName() + "关闭了！");
                }
            }
        }
    }
}
