package cs455.scaling.util;

import java.util.Timer;
import java.util.TimerTask;
import java.nio.channels.SocketChannel;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;



public class Statistics extends TimerTask
{
    //param is used for identifying server side or client side.
    private final int param;
    AtomicLong totalSent = new AtomicLong();
    AtomicLong totalReceived = new AtomicLong();


    //use SocketChannel to trace each client connection status.
    private final ConcurrentHashMap<SocketChannel, LongAdder> map = new ConcurrentHashMap<>();


    public Statistics(int param) {
        this.param = param;
    }

    public void register(SocketChannel client)
    {
      map.put(client, new LongAdder());
    }

    public void deregister(SocketChannel client)
    {
      try
      {
        map.remove(client);
      }
      catch(Exception e)
      {
        System.err.println(e);
      }

    }

    public void increment(SocketChannel client)
    {
      map.computeIfAbsent(client, (v) -> new LongAdder()).increment();
    }

    public void ClientIncrement(int param)
    {
       if(param==1)
       {
         totalSent.getAndIncrement();
       }
       else
       {
         totalReceived.getAndIncrement();
       }
    }

    public void run() {
       if(param==1)
       {
        synchronized(map)
        {
          long sum = 0;
          int count = 0;
          double mean = 0.0;
          int size = 0;
          double std = 0.0;
          for (LongAdder f : map.values())
          {
            sum += f.longValue();
          }

          System.out.println(map);

          for(SocketChannel key: map.keySet())
          {
            if(!key.isOpen()) count++;
          }

          NumberFormat round = new DecimalFormat("#0.000");
          size = map.size()-count;
          if(size>0)
          {
            mean = sum/size;

            double standardDeviation = 0.0;
            for(LongAdder value: map.values()) {
                standardDeviation += Math.pow(value.doubleValue() - mean, 2);
            }
            std = Math.sqrt(standardDeviation/size);

          }

          System.out.println("LocalDateTime : " + LocalDateTime.now() + " Server Throughput: "
                  + sum/20 + " messages/s, Active Client Connections: " + size + ", Mean Per-client Throughput: " 
                  + round.format(mean/20) + " messages/s" + ", Std. Dev. Of Per-cient Throughput: "
                  + round.format(std/20) + " messages/s");
          map.replaceAll((k,v)-> new LongAdder());
        }
      }
       else
       {
         System.out.println("LocalDateTime: " + LocalDateTime.now() + " Total Sent Count:" 
         + totalSent + ", Total Received Count: " + totalReceived);
         totalSent.set(0);
         totalReceived.set(0);
       }
    }


    public static void main(final String args[]) {
        System.out.println("About to schedule");
        final Timer timer = new Timer();
       timer.schedule(new Statistics(1), 0 , 2*1000);
      System.out.println("Task schedule");
    
    
}

}