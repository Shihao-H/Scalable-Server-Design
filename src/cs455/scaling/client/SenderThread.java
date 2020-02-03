package cs455.scaling.client;

import cs455.scaling.util.Hasher;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;


public class SenderThread extends Thread
{
    private final SocketChannel channel;
    private ByteBuffer buffer;
    private final int messageRate;
	private Hasher hasher;
	private final LinkedList<String> hashlist;
	
    public SenderThread(SocketChannel channel, int messageRate, LinkedList<String> hashlist)
    {
    	this.channel=channel;
    	this.messageRate=messageRate;
    	this.hashlist=hashlist;
    }
    
    public void run()
    {
		Random rd = new Random();
		byte[] message = new byte[0x2000];
    	while(channel!=null)
    	{
    		hasher = Hasher.getInstance();
    		try {
        		rd.nextBytes(message);	
    			String str = hasher.SHA1FromBytes(message);
    			buffer = ByteBuffer.allocate(0x2000);
            		buffer = ByteBuffer.wrap(message);	
    			hashlist.add(str);

			} catch (NoSuchAlgorithmException e2) {
				e2.printStackTrace();
			}
        	try {
				channel.write(buffer);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
            buffer.clear();
            
            try {
            	Thread.sleep(1000);
            } catch (InterruptedException e)
            {
			e.printStackTrace();
            }
    	}
    }
       
}

