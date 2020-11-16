package treeChat.threads;

import treeChat.message.Message;
import treeChat.message.MessageType;
import treeChat.message.Packet;
import treeChat.node.Node;
import treeChat.node.NodeInfo;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PingThread implements Runnable{
    private ConcurrentLinkedQueue<Socket> sockets;
    private NodeInfo nodeInfo;
    private ConcurrentLinkedQueue<Packet> packets;

    public PingThread(NodeInfo nodeInfo, ConcurrentLinkedQueue<Socket> sockets, ConcurrentLinkedQueue<Packet> packets){
        this.nodeInfo = nodeInfo;
        this.sockets = sockets;
        this.packets = packets;
    }

    @Override
    public void run() {
        while(true) {
            for (Socket socket : sockets) {
                assert sockets.peek() != null;
                Node.packets.add(new Packet(socket, new Message(MessageType.Ping_MESSAGE, sockets.peek().getInetAddress().toString(), sockets.peek().getPort() + "", UUID.randomUUID())));
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("Error : lulling thread");
                e.printStackTrace();
                System.exit(5);
            }
        }
    }
}
