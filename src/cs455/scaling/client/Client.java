package cs455.scaling.client;

import cs455.scaling.util.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;


public class Client
{
    private SocketChannel client;
    private final Hasher hasher;
    private LinkedList<String> hashlist = new LinkedList<String>();
    private final int messageRate;
    private final int buffersize = 0x2000;
    private Statistics statistics;
    private final Thread senderThread;
    private final Thread receiverThread;

  
    public Client(String serverHost, int serverPort, int messageRate)
    {
        this.messageRate=messageRate;
        this.statistics = new Statistics(2);
    	try
    	{
    		this.client = SocketChannel.open(new InetSocketAddress(serverHost, serverPort));
    	} 
    	catch (IOException e)
    	{
    		System.err.println(e.getMessage());
    	}
    	
      hasher = Hasher.getInstance();

      senderThread = new Thread(() -> {
          Random random = new Random();
          byte[] message = new byte[buffersize];
          while (client != null) {
              random.nextBytes(message);
              try {
            	//   System.out.println(hasher.SHA1FromBytes(message));
            	  hashlist.add(hasher.SHA1FromBytes(message));
				} catch (NoSuchAlgorithmException e1) {
					e1.printStackTrace();
				}
              ByteBuffer buffer = ByteBuffer.wrap(message);
              try {
                  client.write(buffer);
                  this.statistics.ClientIncrement(1);
              } catch (IOException e) {
                  System.err.println(e.getMessage());
              }
              try {
                  Thread.sleep(1000/this.messageRate);
              } catch (InterruptedException e) {
                  System.err.println(e.getMessage());
              }
          }
      });
    	
    receiverThread = new Thread(() -> {
  ByteBuffer receivingBuffer = ByteBuffer.allocate(buffersize);

  while (client != null) {
      try {
          client.read(receivingBuffer);
      } catch (IOException e) {
          System.err.println(e.getMessage());
      }

      receivingBuffer.flip();
      int length = (int) receivingBuffer.get();
      while (receivingBuffer.remaining() >= length) {
          byte[] hashBytes = new byte[length];
          receivingBuffer.get(hashBytes);

          String hash = new String(hashBytes);
          if (hashlist.remove(hash)) {
            this.statistics.ClientIncrement(2);
          } else {
              System.err.println("Wrong hash !!!");
          }

          if (!receivingBuffer.hasRemaining()) break;
          else length = (int) receivingBuffer.get();
      }

      receivingBuffer.compact();
  }

});
}
    
    public static void main(String[] args) throws IOException {
        final int messageRate;
        final int serverPort;
        final String serverHost;
        if (args.length == 3)
        {
            try {
                // serverHost = args[0]+".cs.colostate.edu";
                serverHost = "saturn.cs.colostate.edu";
                serverPort = Integer.parseInt(args[1]);
                messageRate = Integer.parseInt(args[2]);
                Client client = new Client(serverHost,serverPort,messageRate);
                Timer timer = new Timer();
                timer.schedule(client.statistics, 0 , 20*1000);
                client.senderThread.start();
                client.receiverThread.start();
             } catch (NumberFormatException e) {
                 System.err.println("Argument 1 and 2 must be an integer.");
                 System.exit(1);
             }
        }
        else
        {
            System.err.println("Invalid argument size.");
            System.exit(1);
        }
    }

}
