package client;

import java.io.IOException;
import java.net.InetAddress;

public class Main {

    public static void main(String[] args) throws IOException {
        Client client = new Client(InetAddress.getLocalHost(), 3000);

        client.sendMessage();

        client.receiveMessage();

    }

}

