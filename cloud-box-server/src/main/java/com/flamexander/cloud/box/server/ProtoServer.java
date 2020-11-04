package com.flamexander.cloud.box.server;

import com.flamexander.cloud.box.common.ProtoHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProtoServer {
    public static final String SERVER_STORAGE_PATH = "server_repository";

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProtoHandler("server_repository", new ServerCommandReceiver()));
                        }
                    });
            ChannelFuture f = b.bind(8189).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        init();
        new ProtoServer().run();
    }

    public static void init() throws IOException {
        Path serverStoragePath = Paths.get(SERVER_STORAGE_PATH);
        if (!Files.exists(serverStoragePath)) {
            Files.createDirectory(serverStoragePath);
        }
    }
}