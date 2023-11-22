package client;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.util.Scanner;

public class Main {

    private int statusCode;
    private byte[] msg; //max 1484
    private int startLength;
    private int endLength;
    private int amountLength;


    public static void main(String[] args) throws IOException {
        InetAddress address = InetAddress.getLocalHost();
//        InetAddress address = InetAddress.getByName("10.22.33.10");
        int port = 3000;
        DatagramSocket socket = new DatagramSocket();
        sendMessage(address, port, socket);
        DatagramPacket packet;
        byte[] buff2 = new byte[100];
        //byte[] res = new byte[100];
        packet = new DatagramPacket(buff2, 51, address, port);
        socket.receive(packet);
        byte res[] = packet.getData();

        decode(res);


    }

    private static void sendMessage(InetAddress address, int port, DatagramSocket socket) throws IOException {
        Scanner scanner = new Scanner(System.in);
        byte[] buff = scanner.nextLine().getBytes();
        DatagramPacket packet = new DatagramPacket(buff, buff.length, address, port);
        socket.send(packet);
    }


    private static void decode(byte[] byteArray){

        int statusCode = (((int)(byteArray[0]) << 24) & 0xFF000000) |
                (((int)(byteArray[1]) << 16) & 0xFF0000) |
                (((int)(byteArray[2]) << 8) & 0xFF00) |
                ((int)(byteArray[3]) & 0xFF);

        int startLength = (((int)(byteArray[4]) << 24) & 0xFF000000) |
                (((int)(byteArray[5]) << 16) & 0xFF0000) |
                (((int)(byteArray[6]) << 8) & 0xFF00) |
                ((int)(byteArray[7]) & 0xFF);

        int endLength = (((int)(byteArray[8]) << 24) & 0xFF000000) |
                (((int)(byteArray[9]) << 16) & 0xFF0000) |
                (((int)(byteArray[10]) << 8) & 0xFF00) |
                ((int)(byteArray[11]) & 0xFF);

        int amountLength = (((int)(byteArray[12]) << 24) & 0xFF000000) |
                (((int)(byteArray[13]) << 16) & 0xFF0000) |
                (((int)(byteArray[14]) << 8) & 0xFF00) |
                ((int)(byteArray[15]) & 0xFF);

        System.out.println(statusCode + " " + startLength + " " + endLength + " " + amountLength);

    }
}