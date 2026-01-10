package com.meowjang.rubydung.networking;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    public void initialize(String addr, int port) throws IOException {
        this.socket = new Socket(addr, port);
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        System.out.println("Connected to server! :3");
    }

    public void sendBytes(byte[] data) throws IOException {
        out.writeInt(data.length);
        out.write(data);
        out.flush();
    }

    public byte[] receiveBytes() throws IOException {
        int length = in.readInt();
        byte[] data = new byte[length];
        in.readFully(data);
        return data;
    }

    public static void main(String[] args) {
        Client client = new Client();
        Scanner scanner = new Scanner(System.in);
        try {
            client.initialize("127.0.0.1", 5000);

            // Listen for server packets
            new Thread(() -> {
                try {
                    while (true) {
                        byte[] raw = client.receiveBytes();
                        int[] decoded = CreatePacket.decodePacket(raw);
                        System.out.println("\n[Server Packet] Type: " + raw[0] + " Data Length: " + decoded.length);
                    }
                } catch (IOException e) {
                    System.out.println("Connection lost.");
                }
            }).start();

            // Main loop: Create and send a packet based on user input
            while(true) {
                System.out.println("Enter a number to send as data (or 0 to quit):");
                int val = scanner.nextInt();
                if(val == 0) break;

                int[] dataToSend = { val};
                byte[] packet = CreatePacket.createPacket(2, 1, dataToSend);
                client.sendBytes(packet);
                System.out.println("Packet sent! ^w^");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}