package com.sucl.zookeeper.service.loadbalancing.server;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

/**
 * 处理信道事件
 * @author sucl
 * @date 2019/6/6
 */
@Data
public class ServerHandler extends ChannelHandlerAdapter {

    private IncreaseOrDecreaseLoadOperator loadOperator;

    public static final int BALANCE_STEP = 1;

    public ServerHandler(IncreaseOrDecreaseLoadOperator loadOperator){
        this.loadOperator = loadOperator;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        super.channelActive(ctx);
        loadOperator.increaseLoad(BALANCE_STEP);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        super.channelInactive(ctx);
        loadOperator.decreaseLoad(BALANCE_STEP);
    }
}
