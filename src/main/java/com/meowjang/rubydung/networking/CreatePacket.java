package com.meowjang.rubydung.networking;

import java.nio.ByteBuffer;

public class CreatePacket {

    /**
     * Creates a packet where data elements are full 4-byte integers
     */
    public static byte[] createPacket(int packetType, int target, int[] data) {
        // Size: 1 byte (type) + 1 byte (target) + (4 bytes * number of ints)
        int packetSize = 1 + 1 + (data.length * 4);

        ByteBuffer buffer = ByteBuffer.allocate(packetSize);

        buffer.put((byte) packetType);
        buffer.put((byte) target);

        for (int value : data) {
            buffer.putInt(value); // Automatically handles the 4-byte conversion ^w^
        }

        return buffer.array();
    }

    /**
     * Decodes the packet back into a structure
     */
    public static int[] decodePacket(byte[] packet) {
        ByteBuffer buffer = ByteBuffer.wrap(packet);

        // Read headers
        int type = buffer.get() & 0xFF;
        //
        int target = buffer.get() & 0xFF;

        // Calculate how many integers are left in the payload
        int remainingInts = (packet.length - 2) / 4;
        int[] data = new int[remainingInts+1];
        data[0]=target;

        for (int i = 0; i < remainingInts; i++) {
            data[i+1] = buffer.getInt();
        }

        // You might want to return a custom Object here containing
        // type, target, and data, but for now we'll just return data :3
        return data;
    }
}