package treeChat.threads;

import treeChat.message.Message;
import treeChat.message.MessageType;
import treeChat.message.Packet;
import treeChat.node.Node;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

import static treeChat.message.MessageType.Chat_MESSAGE;

public class ReceiveThread implements Runnable{

    public void run() {
        while (true) {
            ConcurrentLinkedQueue<Message> recvPackets = Node.receivedPackets;
            try {
                //System.out.println("i try");
                DatagramPacket recvPacket = new DatagramPacket(new byte[2048], 2048);
                SenderThread.datagramSocket.receive(recvPacket);
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(recvPacket.getData()));
                Message gotMessage = (Message) ois.readObject();
                receive(gotMessage, recvPacket);
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void receive(Message message, DatagramPacket packet) {
        if (Node.nodeInfo.getPercentLoss() > ThreadLocalRandom.current().nextInt(0, 100)) return;
        switch (message.getType()) {
            case Chat_MESSAGE :
                boolean contains = false;
                for (Message message1 : Node.receivedPackets) {
                    if (message1.getUuid().equals(message.getUuid())) {
                        contains = true;
                        break;
                    }
                }
                if (contains) {
                    Message confirmMessage = new Message(MessageType.Confirmation_MESSAGE, Node.nodeInfo.getName(), "accepted", message.getUuid());
                    for (Socket socket : Node.sockets) {
                        if (socket.getPort() == packet.getPort()) {
                            Node.packets.add(new Packet(socket, confirmMessage));
                            break;
                        }
                    }
                } else {
                    for (Socket socket : Node.sockets) {
                        if (socket.getPort() == packet.getPort()) {
                            Message confirmMessage = new Message(MessageType.Confirmation_MESSAGE, Node.nodeInfo.getName(), "accepted", message.getUuid());
                            Node.packets.add(new Packet(socket, confirmMessage));
                        } else {
                            Message newMessage = new Message(Chat_MESSAGE, message.getName(), message.getText(), UUID.randomUUID());
                            Node.packets.add(new Packet(socket, newMessage));
                        }
                    }
                    Node.receivedPackets.add(message);
                    System.out.println(message.getName() + " : " + message.getText());
                }
                break;
            case Confirmation_MESSAGE:
                Packet packet1 = null;
                for (Packet packet2 : Node.packets) {
                    if (packet2.getMessage().getUuid() == message.getUuid()) {
                        packet1 = packet2;
                        break;
                    }
                }
                Node.packets.remove(packet1);
                break;
            case Ping_MESSAGE:
                //System.out.println("PING FROM " + packet.getPort());
                boolean key = false;
                for (Socket socket : Node.sockets) {
                    if (socket.getPort() == packet.getPort())  {
                        key = true;
                        break;
                    }
                }
                if (!key) {
                    try {
                        Socket socket = new Socket(String.copyValueOf(packet.getAddress().toString().toCharArray(), 1, packet.getAddress().toString().length() - 1), packet.getPort());
                        Node.sockets.add(socket);
                        Node.time.put(socket, System.currentTimeMillis());
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("SDSDSA");
                        System.exit(404);
                    }
                }
                for (Socket socket : Node.time.keySet()) {
                    if (socket.getPort() == packet.getPort()) {
                        Node.time.replace(socket, System.currentTimeMillis());
                    }
                }
                if (!(message.getName().equals("/" + packet.getAddress().getHostAddress()) && Integer.parseInt(message.getText()) == Node.nodeInfo.getPort())) {
                    Node.nextIP = message.getName();
                    Node.nextPort = Integer.parseInt(message.getText());
                } else {
                    Node.nextIP = null;
                    Node.nextPort = null;
                }
                break;
        }
    }
}
