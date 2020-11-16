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
import java.util.concurrent.ThreadLocalRandom;

import static treeChat.message.MessageType.Chat_MESSAGE;

public class ReceiveThread implements Runnable{

    public void run() {
        while (true) {
            try {
                DatagramPacket recvPacket = new DatagramPacket(new byte[2048], 2048);
                SenderThread.datagramSocket.receive(recvPacket);
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(recvPacket.getData()));
                Message gotMessage = (Message) ois.readObject();
                parseMessage(gotMessage, recvPacket);
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseMessage(Message message, DatagramPacket packet) {
        if (Node.nodeInfo.getPercentLoss() > ThreadLocalRandom.current().nextInt(0, 100)) return;
        switch (message.getType()) {
            case Chat_MESSAGE :
                parseChatMessage(message, packet);
                break;
            case Confirmation_MESSAGE:
                parseConfirmationMessage(message);
                break;
            case Ping_MESSAGE:
                parsePingMessage(message, packet);
                break;
        }
    }

    private void parsePingMessage(Message message, DatagramPacket packet) {
        boolean key = searchSocket(packet);
        if (!key) addSocket(packet);
        updateSocketTimer(packet);
        parseNextHost(message, packet);
    }

    private void parseNextHost(Message message, DatagramPacket packet) {
        if (!(message.getName().equals("/" + packet.getAddress().getHostAddress()) && Integer.parseInt(message.getText()) == Node.nodeInfo.getPort())) {
            Node.nextIP = message.getName();
            Node.nextPort = Integer.parseInt(message.getText());
        } else {
            Node.nextIP = null;
            Node.nextPort = null;
        }
    }

    private void updateSocketTimer(DatagramPacket packet) {
        for (Socket socket : Node.time.keySet())
            if (socket.getPort() == packet.getPort())
                Node.time.replace(socket, System.currentTimeMillis());
    }

    private void addSocket(DatagramPacket packet) {
        try {
            Socket socket = new Socket(String.copyValueOf(packet.getAddress().toString().toCharArray(), 1, packet.getAddress().toString().length() - 1), packet.getPort());
            Node.sockets.add(socket);
            Node.time.put(socket, System.currentTimeMillis());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error : creating socket \n parsePingMessage");
            System.exit(404);
        }
    }

    private boolean searchSocket(DatagramPacket packet) {
        for (Socket socket : Node.sockets)
            if (socket.getPort() == packet.getPort())
                return true;
        return false;
    }

    private void parseConfirmationMessage(Message message) {
        Packet packet1 = null;
        for (Packet packet2 : Node.packets) {
            if (packet2.getMessage().getUuid() == message.getUuid()) {
                packet1 = packet2;
                break;
            }
        }
        Node.packets.remove(packet1);
    }

    private void parseChatMessage(Message message, DatagramPacket packet) {
        boolean contains = searchChatMessage(message);
        if (contains) {
            sendConfirmMessage(message, packet);
        } else {
            parseNewChatMessage(message, packet);
        }
    }

    private void parseNewChatMessage(Message message, DatagramPacket packet) {
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

    private void sendConfirmMessage(Message message, DatagramPacket packet) {
        Message confirmMessage = new Message(MessageType.Confirmation_MESSAGE, Node.nodeInfo.getName(), "accepted", message.getUuid());
        for (Socket socket : Node.sockets) {
            if (socket.getPort() == packet.getPort()) {
                Node.packets.add(new Packet(socket, confirmMessage));
                break;
            }
        }
    }

    private boolean searchChatMessage(Message message) {
        for (Message message1 : Node.receivedPackets)
            if (message1.getUuid().equals(message.getUuid()))
                return true;
        return false;
    }
}
