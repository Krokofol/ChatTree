package treeChat.node;

import treeChat.message.Message;
import treeChat.message.MessageType;
import treeChat.message.Packet;
import treeChat.threads.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

public class Node {

    public static String nextIP;
    public static Integer nextPort;

    public static ServerSocket serverSocket;
    public static ConcurrentLinkedQueue<Socket> sockets;
    public static ConcurrentLinkedQueue<Packet> packets;
    public static ConcurrentLinkedQueue<Message> receivedPackets;
    public static ConcurrentHashMap<Socket, Long> time = new ConcurrentHashMap<>();
    public static NodeInfo nodeInfo;

    public static void main(String[] args) {
        parseInput(args);
        creatServer(args);
        connecting(args);
        createReader();
        createPinger();
        createSender();
        createReceiver();
        createCheacker();
        enterTheChat(args, nodeInfo.getName() + " entered the chat");
    }

    private static void enterTheChat(String[] args, String text) {
        if (args.length == 5) {
            Message message = new Message(MessageType.Chat_MESSAGE, "    SERVER", text, UUID.randomUUID());
            packets.add(new Packet(sockets.peek(), message));
        }
    }

    private static void createCheacker() {
        new Thread(new CheсkingThread()).start();
    }

    private static void createReceiver() {
        new Thread(new ReceiveThread()).start();
    }

    private static void createReader(){
        new Thread(new InputThread(nodeInfo, sockets, packets)).start();
    }

    private static void createSender(){
        new Thread(new SenderThread(packets, nodeInfo)).start();
    }

    private static void createPinger(){
        new Thread(new PingThread(nodeInfo, sockets, packets)).start();
    }

    private static void parseInput(String[] args) {
        packets = new ConcurrentLinkedQueue<Packet>();
        receivedPackets = new ConcurrentLinkedQueue<Message>();
        if (args.length != 5 && args.length != 3) {
            System.out.println("incorrect input : \ncorrect input : \n    name, percent_loss, port \n    name, percent_loss, port, host_ip, host_port");
            System.exit(1);
        }
        nodeInfo = new NodeInfo(args);
    }

    private static void creatServer(String[] args) {
        sockets = new ConcurrentLinkedQueue<Socket>();
        try {
            serverSocket = new ServerSocket(Integer.parseInt(args[2]));
        } catch (IOException e) {
            System.out.println("Error : creating serverSocket");
            e.printStackTrace();
            System.exit(2);
        }
        new Thread(new ServerThread(serverSocket)).start();
    }

    private static void connecting(String[] args) {
        if (args.length == 5) {
            try {
                Socket socket = new Socket(args[3], Integer.parseInt(args[4]));
                sockets.add(socket);
                time.put(socket, System.currentTimeMillis());
            } catch (IOException e) {
                System.out.println("Error : creating socket\nconnecting to host");
                e.printStackTrace();
                System.exit(3);
            }
        }
    }

    public static void connecting(String ip, int port) {
        try {
            Socket socket = new Socket(ip, port);
            sockets.add(socket);
            Node.nodeInfo.setPort(socket.getLocalPort());
            time.put(socket, System.currentTimeMillis());
            enterTheChat(new String[5], nodeInfo.getName() + " reconnected");
        } catch (IOException e) {
            System.out.println("Error : creating socket\nconnecting to host");
            e.printStackTrace();
            System.exit(3);
        }
    }

    public static void acceptConnecting(Socket socket) {
        //sockets.add(socket);
        //time.put(socket, System.currentTimeMillis());
    }

}
