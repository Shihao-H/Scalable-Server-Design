package cs455.scaling.server.task;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.nio.channels.SelectionKey;
import cs455.scaling.util.Hasher;
import cs455.scaling.util.Statistics;

public class Sender implements Task {


    private static Hasher hasher;
    private SocketChannel client;
    private ByteBuffer buffer;
    private SelectionKey key;

    public Sender(SocketChannel client, ByteBuffer buffer, SelectionKey key)
    {
        this.client = client;
        this.buffer = buffer;
        this.key = key;
    }

    @Override
    public void execute() throws IOException
    {
        // TODO Auto-generated method stub
        hasher = Hasher.getInstance(); 
        try
        {
            String back = hasher.SHA1FromBytes(buffer.array());
            ByteBuffer ackBytes = ByteBuffer.allocate(back.length () + 1);
            ackBytes.put((byte)back.length());
            ackBytes.put(back.getBytes());
            ackBytes.flip();
            synchronized(client)
            {
                try
                {
                    client.write(ackBytes);
                }
                catch(Exception e)
                {
                    ((Statistics)key.attachment()).deregister(client);
                }
            }
            ((Statistics)key.attachment()).increment(client);
            key.attach(null);
            // // Clear the buffer
            // buffer.clear();
        } 
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }    

    }


}