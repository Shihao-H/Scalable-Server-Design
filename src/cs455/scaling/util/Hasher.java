package cs455.scaling.util;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;


public class Hasher {
	public static Hasher SingletonInstance = new Hasher();
	
	public static Hasher getInstance()
	{
		return SingletonInstance;
	}
	
	private Hasher()
	{}
	
    public String SHA1FromBytes(byte[] data) throws NoSuchAlgorithmException {
    	 MessageDigest digest = MessageDigest.getInstance("SHA1");
    	 byte[] hash = digest.digest(data);
    	 BigInteger hashInt = new BigInteger(1, hash);
    	 return hashInt.toString(16);
    }

}
