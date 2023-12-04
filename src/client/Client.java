package client;

import server.Message;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class Client{
    private  int statusCode;
    private  byte[] fileBytes = new byte[1484]; //max 1484
    private  int startLength;
    private  int endLength;
    private  int amountLength;

    private  int packetsAmount;
    private byte[] recivedMsg;
    private String fileName;

    InetAddress address;
    int port;
    DatagramSocket socket;
    ArrayList<Message> data = new ArrayList<>();

    public Client(InetAddress address, int port){
        this.address = address;
        this.port = port;
        try {
            this.socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public void receiveMessage(){
        DatagramPacket packet;
        byte[] buff2 = new byte[1500];
        packet = new DatagramPacket(buff2, 1500, address, port);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        recivedMsg = packet.getData();
        decode(recivedMsg);
    }

    public void sendMessage() {
        try {
            Scanner scanner = new Scanner(System.in);
            String cmd = scanner.nextLine();
            byte[] buff = cmd.getBytes();
            fileName = cmd.split("\\s+")[1];
            DatagramPacket packet = new DatagramPacket(buff, buff.length, address, port);
            socket.send(packet);
        }catch (IOException e){
            System.out.println("Exception while sending command");
        }
    }

    private void decode(byte[] byteArray) {

        int statusCode = (((int) (byteArray[0]) << 24) & 0xFF000000) |
                (((int) (byteArray[1]) << 16) & 0xFF0000) |
                (((int) (byteArray[2]) << 8) & 0xFF00) |
                ((int) (byteArray[3]) & 0xFF);

        int startLength = (((int) (byteArray[4]) << 24) & 0xFF000000) |
                (((int) (byteArray[5]) << 16) & 0xFF0000) |
                (((int) (byteArray[6]) << 8) & 0xFF00) |
                ((int) (byteArray[7]) & 0xFF);

        int endLength = (((int) (byteArray[8]) << 24) & 0xFF000000) |
                (((int) (byteArray[9]) << 16) & 0xFF0000) |
                (((int) (byteArray[10]) << 8) & 0xFF00) |
                ((int) (byteArray[11]) & 0xFF);

        int amountLength = (((int) (byteArray[12]) << 24) & 0xFF000000) |
                (((int) (byteArray[13]) << 16) & 0xFF0000) |
                (((int) (byteArray[14]) << 8) & 0xFF00) |
                ((int) (byteArray[15]) & 0xFF);

        this.statusCode = statusCode;
        this.startLength = startLength;
        this.endLength = endLength;
        this.amountLength = amountLength;

        System.arraycopy(byteArray, 16, fileBytes,0, (amountLength - startLength));

        proceedStatusCode();

    }

    private void proceedStatusCode(){
        switch (statusCode){
            case 100: {
                this.packetsAmount = Integer.parseInt(new String(recivedMsg, startLength + 16, endLength).substring(8));
                startRecieveFile();
                //System.out.println(packetsAmount);
                break;
            }
            case 101:{
                Message msg = new Message(statusCode, startLength, endLength, amountLength,fileBytes.clone());
                data.add(msg);
                break;
            }
            case 404:{
                System.out.println("No such file exists");
                break;
            }
        }
    }

    private void startRecieveFile() {
        for(int i = 0; i < packetsAmount; i++){
            System.out.println("Recivieng packet: " + i);
            byte[] buff = new byte[1500];
            DatagramPacket packet = new DatagramPacket(buff, 1500, address, port);
            try {socket.receive(packet);} catch (IOException e) {throw new RuntimeException(e);}
            decode(packet.getData());
            System.out.println(packet.getLength());
        }
        data.sort(Comparator.comparingInt(Message::getStartLength));
        createFile();
    }

    private void createFile(){
        byte[] output = new byte[data.get(data.size()-1).getAmountLength()];
        for(int i = 0; i < data.size(); i++){
            int start = data.get(i).getStartLength();
            int end =  data.get(i).getAmountLength();
            int length = end - start;
            System.arraycopy(data.get(i).getMsg(), 0, output, start, length);
        }
        String filePath = "clFiles/" + fileName;
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(output);
            System.out.println("File created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}