package treeChat.threads;

import treeChat.message.MessageType;
import treeChat.message.Packet;
import treeChat.node.Node;
import treeChat.node.NodeInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SenderThread implements Runnable {
    private ConcurrentLinkedQueue<Packet> packets;
    private NodeInfo nodeInfo;
    public static DatagramSocket datagramSocket;

    public SenderThread(ConcurrentLinkedQueue<Packet> packets, NodeInfo nodeInfo) {
        this.packets = packets;
        this.nodeInfo = nodeInfo;
        try {
            datagramSocket = new DatagramSocket(nodeInfo.getPort());
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.out.println("Error : lulling thread");
                e.printStackTrace();
                System.exit(6);
            }
            for (Packet packet : Node.packets) {
                if (packet == null) break;
                InetSocketAddress address = new InetSocketAddress(packet.getSocket().getInetAddress(), packet.getSocket().getPort());
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(packet.getMessage());
                    DatagramPacket dPacket = new DatagramPacket(baos.toByteArray(), baos.toByteArray().length, address);
                    datagramSocket.send(dPacket);
                    if (packet.getMessage().getType() != MessageType.Chat_MESSAGE) {
                        Node.packets.remove(packet);
                    }
                } catch (SocketException e) {
                    System.out.println("Error : creating datagramSocket");
                    e.printStackTrace();
                    System.exit(7);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
