package cs455.scaling.server.task;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import cs455.scaling.server.ThreadPoolMG;
import cs455.scaling.util.*;

public class Receiver implements Task {
    private final SelectionKey key;
    private ThreadPoolMG manager;

    public Receiver(SelectionKey key, ThreadPoolMG manager) {
        this.key = key;
        this.manager = manager;
    }

    @Override
    public void execute() throws IOException {

        // Create a buffer to read into
        ByteBuffer buffer = ByteBuffer.allocate(0x2000);
        // Grab the socket from the key
        SocketChannel client = (SocketChannel) key.channel();
        int bytesRead = 0;
        try {
            while (buffer.hasRemaining() && bytesRead != -1) {
                // Read from it
                bytesRead = client.read(buffer);
            }
            // Handle a closed connection
            if (bytesRead == -1) {
                ((Statistics)key.attachment()).deregister(client);
                try {
                    client.close();
                } catch (Exception e) {
                    System.err.println("Unable to close client connection");
                    return;
                }
                System.out.println("\t\tClient disconnected.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            manager.addTask(new Sender(client, buffer, key));
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } 
    }
}