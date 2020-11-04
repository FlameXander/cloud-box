package com.flamexander.cloud.box.server;

import com.flamexander.cloud.box.common.CommandReceiver;
import com.flamexander.cloud.box.common.ProtoFileSender;
import io.netty.channel.ChannelHandlerContext;

import java.nio.file.Paths;

public class ServerCommandReceiver extends CommandReceiver {
    @Override
    public void parseCommand(ChannelHandlerContext ctx, String cmd) throws Exception {
        if(cmd.startsWith("/request ")) {
            String fileToClientName = cmd.split("\\s")[1];
            ProtoFileSender.sendFile(Paths.get("server_repository", fileToClientName), ctx.channel(), null);
        }
    }
}
