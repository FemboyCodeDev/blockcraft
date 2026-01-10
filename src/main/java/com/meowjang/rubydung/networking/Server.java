package com.meowjang.rubydung.networking;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    // We keep a list so we can broadcast messages to everyone!
    private List<ClientHandler> clients = new ArrayList<>();

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port + " ^w^");

            while (true) {
                // This blocks until someone joins
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getRemoteSocketAddress());

                // Create a handler for this specific girl/client
                ClientHandler clientHandler = new ClientHandler(socket);
                clients.add(clientHandler);

                // Start a new thread so we can go back to waiting for more clients
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcast(byte[] data) {
        // We use a synchronized block or copy to avoid errors if someone disconnects while we are looping
        synchronized (clients) {
            for (ClientHandler client : clients) {
                try {
                    client.sendBytes(data);
                } catch (IOException e) {
                    // If we can't send to them, they probably disconnected
                    System.out.println("Failed to broadcast to a client.");
                }
            }
        }
    }

    // Inner class to handle individual client communication
    private class ClientHandler implements Runnable {
        private Socket socket;
        private DataInputStream in;
        private DataOutputStream out;

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            this.out = new DataOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            try {
                // Send initial welcome packet just to this client
                int[] spawnData = {100, 64, 100};
                sendBytes(CreatePacket.createPacket(1, 0, spawnData));

                while (true) {
                    // Receive data
                    int length = in.readInt();
                    byte[] raw = new byte[length];
                    in.readFully(raw);

                    // Decode the packet into our int array
                    int[] decoded = CreatePacket.decodePacket(raw);

                    // --- DISPLAY LOGIC ^w^ ---
                    System.out.println("\n--- Incoming Packet ---");
                    System.out.println("From: " + socket.getRemoteSocketAddress());
                    System.out.println("Raw Type: " + raw[0]);
                    System.out.print("Decoded Data: ");

                    for (int i = 0; i < decoded.length; i++) {
                        System.out.print(decoded[i] + (i < decoded.length - 1 ? ", " : ""));
                    }
                    System.out.println("\n-----------------------");
                    // -------------------------

                    // Broadcast to everyone else!
                    broadcast(raw);
                }
            } catch (IOException e) {
                System.out.println("Client disconnected :c");
            } finally {
                cleanup();
            }
        }

        public void sendBytes(byte[] data) throws IOException {
            out.writeInt(data.length);
            out.write(data);
            out.flush();
        }

        private void cleanup() {
            try {
                clients.remove(this);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start(5000);
    }
}