package com.flamexander.cloud.box.client;

import com.flamexander.cloud.box.common.CloudBoxCommandsList;
import com.flamexander.cloud.box.common.ProtoFileSender;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class ClientApp {
    public static void main(String[] args) throws Exception {
        CountDownLatch connectionOpened = new CountDownLatch(1);
        new Thread(() -> Network.getInstance().start(connectionOpened)).start();
        connectionOpened.await();

        Scanner sc = new Scanner(System.in);
        while (true) {
            String cmd = sc.nextLine();
            if (cmd.equals("/exit")) {
                break;
            }
            if (cmd.startsWith("/send ")) {
                String filename = cmd.split("\\s")[1];
                Path filePath = Paths.get("client_repository", filename);
                if (!Files.exists(filePath)) {
                    System.out.println("Файл для отправки не найден в репозитории");
                    continue;
                }
                ProtoFileSender.sendFile(filePath, Network.getInstance().getCurrentChannel(), future -> {
                    if (!future.isSuccess()) {
                        System.out.println("Не удалось отправить файл на сервер");
                        future.cause().printStackTrace();
                    }
                    if (future.isSuccess()) {
                        System.out.println("Файл успешно передан");
                    }
                });
                continue;
            }
            if (cmd.startsWith("/download ")) {
                String filename = cmd.split("\\s")[1];
                sendFileRequest(filename, Network.getInstance().getCurrentChannel());
                continue;
            }
            System.out.println("Введена неверная команда, попробуйте снова");
        }
    }

    public static void sendFileRequest(String filename, Channel outChannel) {
        byte[] filenameBytes = ("/request " + filename).getBytes(StandardCharsets.UTF_8);
        ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(1 + 4 + filenameBytes.length);
        buf.writeByte(CloudBoxCommandsList.CMD_SIGNAL_BYTE);
        buf.writeInt(filenameBytes.length);
        buf.writeBytes(filenameBytes);
        outChannel.writeAndFlush(buf);
    }
}