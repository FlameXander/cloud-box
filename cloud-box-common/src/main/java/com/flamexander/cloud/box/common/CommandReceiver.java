package com.flamexander.cloud.box.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public abstract class CommandReceiver {
    public enum State {
        IDLE, COMMAND_LENGTH, COMMAND
    }

    private State currentState = State.IDLE;
    private int commandTypeLength;
    private int receivedLength;
    private StringBuilder cmd;

    public void startReceive() {
        currentState = State.COMMAND_LENGTH;
        cmd = new StringBuilder();
    }

    public void receive(ChannelHandlerContext ctx, ByteBuf buf, Runnable finishOperation) throws Exception {
        if (currentState == State.COMMAND_LENGTH) {
            if (buf.readableBytes() >= 4) {
                System.out.println("STATE: Get command length");
                commandTypeLength = buf.readInt();
                currentState = State.COMMAND;
                receivedLength = 0;
                cmd.setLength(0);
            }
        }
        if (currentState == State.COMMAND) {
            while (buf.readableBytes() > 0) {
                cmd.append((char)buf.readByte()); // todo а как же кириллица?
                receivedLength++;
                if(receivedLength == commandTypeLength) {
                    parseCommand(ctx, cmd.toString());
                    currentState = State.IDLE;
                    finishOperation.run();
                    return;
                }
            }
        }
    }

    public abstract void parseCommand(ChannelHandlerContext ctx, String cmd) throws Exception;
}
