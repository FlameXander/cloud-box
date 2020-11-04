package com.flamexander.cloud.box.client;

import com.flamexander.cloud.box.common.CommandReceiver;
import io.netty.channel.ChannelHandlerContext;

import javax.naming.OperationNotSupportedException;

public class ClientCommandReceiver extends CommandReceiver {
    @Override
    public void parseCommand(ChannelHandlerContext ctx, String cmd) throws Exception {
        throw new OperationNotSupportedException("Мы не должны сюда попадать на клиенте");
    }
}
