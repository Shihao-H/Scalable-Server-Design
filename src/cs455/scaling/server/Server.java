package cs455.scaling.server;

import cs455.scaling.server.task.*;
import cs455.scaling.util.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;



public class Server
{
    private ThreadPoolMG manager;
    private final int port;
    private Statistics statistics;

	public Server(int port, int threadPoolsize)
	{
        this.port = port;
        this.statistics = new Statistics(1);//1 means Server side statistics
        this.manager = new ThreadPoolMG(threadPoolsize, 1, 1); 
    }
	
	
	private void init() throws IOException, InterruptedException 
	{
        Timer timer = new Timer();
        timer.schedule(this.statistics, 0 , 20*1000);//20 second timeframe


		Selector selector = Selector.open();
        // Create our input channel
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress("saturn.cs.colostate.edu", this.port));
        serverSocket.configureBlocking(false);
        // Register our channel to the selector
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
 
        // Loop on selector
        while (true) {
            selector.selectNow();
            // Key(s) are ready
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            // Loop over ready keys
            Iterator<SelectionKey> iter = selectedKeys.iterator();
            while (iter.hasNext()) {
                // Grab current key
                SelectionKey key = iter.next();
                
                // Optional
                if (key.isValid() == false) { 
                    continue; 
                }

                // New connection on serverSocket
                if (key.isAcceptable() && key.attachment() == null) {
                   key.attach(this.statistics);
                   manager.addTask(new Register(key, selector, serverSocket));
                }
 
                // Previous connection has data to read
                if (key.isReadable() && key.attachment() == null) {
                    key.attach(this.statistics);
                    manager.addTask(new Receiver(key,this.manager));
                }

                // Remove it from our set
                iter.remove();
            }
        }
    }
    

    public static void main(String[] args) throws IOException, InterruptedException
    {
        final int poolsize;
        final int port;
        if (args.length == 4)
        { 
            try {
                port = Integer.parseInt(args[0]);
                poolsize = Integer.parseInt(args[1]);
                Server server = new Server(port,poolsize);
                server.init();
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
