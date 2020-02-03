package cs455.scaling.server;

import cs455.scaling.server.task.*;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;


public class WorkerThread implements Runnable
{
	private final LinkedBlockingQueue<Task> queue; 
    public WorkerThread(LinkedBlockingQueue<Task> q)
    {
		this.queue = q;
    }
	  @Override
    public void run()
    {
    	Task task;
    	while(true)
    	{
    		try
    		{
				//retrieve and remove the head of this queue. 
				//If the queue is empty then it will wait until an element becomes available. 
				task = queue.take();
				try
				{
					task.execute();
					// System.out.println("thread-"+this.seqNum+task);
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
    		catch (InterruptedException e)
    		{
				break;
			}
    		
    	}
    }
 
}
