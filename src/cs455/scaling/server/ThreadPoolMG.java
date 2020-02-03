package cs455.scaling.server;

import cs455.scaling.server.task.Task;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;


public class ThreadPoolMG {
	private final LinkedBlockingQueue<Task> queue; 
	// private final LinkedList<byte[]> buffer;
	private final Thread[] threads;
	private final int batchSize;
	private final int batchTime;

	
	public ThreadPoolMG(int nThread,int batchSize, int batchTime)
	{
		this.batchSize = batchSize;
		this.batchTime = batchTime;
		this.queue = new LinkedBlockingQueue<Task>();
		this.threads = new Thread[ nThread ];
		for(int i=0;i<nThread;i++)
		{
			threads[i] = new Thread(new WorkerThread(queue));
			threads[i].start();
		}
	}
	
	public void addTask(Task task) throws InterruptedException
	{
		queue.put(task);
	}
	
	public int getSize()
	{
		return this.queue.size();
	}
    
}