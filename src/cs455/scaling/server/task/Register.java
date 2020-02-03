package cs455.scaling.server.task;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import cs455.scaling.util.Statistics;

public class Register implements Task {

    private SelectionKey key;
    private final Selector selector;
    private final ServerSocketChannel serverSocket;
    public Register(SelectionKey key, Selector selector, ServerSocketChannel serverSocket)
    {
        this.key = key;
        this.selector = selector;
        this.serverSocket = serverSocket;
    }

    @Override
    public void execute() throws IOException
    {
        SocketChannel client = this.serverSocket.accept();
        if(client==null)
        {
            key.attach(null);
        	return;
        }
        // Configure it to be a new channel and key that our selector should monitor
        client.configureBlocking(false);
        // selector.wakeup();
        client.register(this.selector, SelectionKey.OP_READ);
        ( ( Statistics ) this.key.attachment()).register(client);
        key.attach(null);		
    }
}