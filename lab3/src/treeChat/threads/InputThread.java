package treeChat.threads;

import treeChat.message.Message;
import treeChat.message.MessageType;
import treeChat.message.Packet;
import treeChat.node.Node;
import treeChat.node.NodeInfo;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InputThread implements Runnable{
    private NodeInfo nodeInfo;
    private Scanner inputStream;
    private ConcurrentLinkedQueue<Socket> sockets;
    private ConcurrentLinkedQueue<Packet> packets;

    public InputThread(NodeInfo nodeInfo, ConcurrentLinkedQueue<Socket> sockets, ConcurrentLinkedQueue<Packet> packets) {
        this.nodeInfo = nodeInfo;
        this.sockets = sockets;
        this.packets = packets;
        inputStream = new Scanner(System.in);
    }

    @Override
    public void run() {
        while (true) {
            String input = inputStream.nextLine();
            Message message;
            for (Socket socket : sockets) {
                message = new Message(MessageType.Chat_MESSAGE, nodeInfo.getName(), input, UUID.randomUUID());
                Node.packets.add(new Packet(socket, message));
            }
        }
    }
}
