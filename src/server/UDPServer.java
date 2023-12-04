package server;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;

public class UDPServer implements Runnable{
    DatagramSocket serverSocket;
    boolean work = true;
    int port;
    public UDPServer(int port){
        this.port = port;
        try {
            serverSocket = new DatagramSocket(port);
        }catch (SocketException e){
            System.out.println("Socket wasn't created: " + e.getMessage());
        }
    }
    @Override
    public void run() {
        while (work) {
            try {
                byte[] buff = new byte[1000];
                DatagramPacket packet = new DatagramPacket(buff, buff.length);
                serverSocket.receive(packet);
                String[] data = new String(packet.getData(), 0, packet.getLength()).split("\\s+");
                if(data[0].equalsIgnoreCase("HI")){
                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
                    new Thread(new Connection(address, port, data[1])).start();
                }
                System.out.println(new String(packet.getData(),0,packet.getLength()));
            }catch (IOException e){
                System.out.println("Could not receive packet");
            }
        }
    }
}
//StatusCode | start length | end length | length amount | bytes msg
class Connection implements Runnable{
    public static final String EXCEPTION_IN_SENDING_PACKET = "Exception in sending packet: ";
    InetAddress address;
    DatagramSocket clSocket;
    int port;
    boolean work = true;
    private String fileName;
    private byte[] fileBytes;
    private boolean status;
    File dir;

    public Connection(
            InetAddress address,
            int port,
            String fileName
    ){
        this.address = address;
        this.port = port;
        this.fileName = fileName;
        createSocket();

//        try{
//            dir = new File("files");
//        }catch (IOException e){
//            System.out.println();
//        }

        getFile(fileName);
    }

    @Override
    public void run() {
        String msgText;
        DatagramPacket packet;



        if (!status) {
            msgText = "No file was found with name provided";
            Message msg = new Message(404, 0, msgText.length(), msgText.length(), msgText.getBytes());
            packet = new DatagramPacket(msg.getMsgByteArray(), 0, msg.getMsgByteArray().length, address, port);
            try {clSocket.send(packet);} catch (IOException e) {System.out.println(EXCEPTION_IN_SENDING_PACKET + e.getMessage());}
        } else {
            int maxBytes = 1500 - 16;
            int packetsAmount = Math.ceilDiv(fileBytes.length, maxBytes);
            byte[][] packetData = new byte[packetsAmount][maxBytes];
            for (int i = 0; i < packetsAmount; i++) {
                int startIndex = i * maxBytes;
                int endIndex = Math.min(startIndex + maxBytes, fileBytes.length);
                int packetSize = endIndex - startIndex;
                packetData[i] = new byte[packetSize];
                System.arraycopy(fileBytes, startIndex, packetData[i], 0, packetSize);
            }

            msgText = "Packets:" + packetsAmount;
            Message msg = new Message(100, 0, msgText.length(), msgText.length(), msgText.getBytes());
            packet = new DatagramPacket(msg.getMsgByteArray(), 0, msg.getMsgByteArray().length, address, port);
            try {clSocket.send(packet);} catch (IOException e) {System.out.println(EXCEPTION_IN_SENDING_PACKET + e.getMessage());}

            for(int i = 0, startLength = 0; i < packetsAmount; i++){
                Message data = new Message(101, startLength, packetData[i].length, startLength + packetData[i].length, packetData[i]);
                packet = new DatagramPacket(data.getMsgByteArray(), 0, data.getMsgByteArray().length, address, port);
                try {clSocket.send(packet);} catch (IOException e) {System.out.println(EXCEPTION_IN_SENDING_PACKET + e.getMessage());}
                startLength += packetData[i].length;
            }
            System.out.println("end");
        }
    }


    private void getFile(String fileName) {
        try {
            status = true;
            fileBytes = Files.readAllBytes(Path.of("files/"+ fileName));
        }catch (IOException e){
            status = false;
            System.out.println("Could not get file");
        }
    }

    private void createSocket() {
        try {
            clSocket = new DatagramSocket();

        }catch (IOException e){

            System.out.println("Could not create socket");
        }
    }
}
