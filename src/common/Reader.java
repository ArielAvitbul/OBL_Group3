package common;

import java.net.InetAddress;

/**
 * This class represents a reader in the system.
 * all users are reader before they login to the system.
 * @author Good Guy
 *
 */
public class Reader {

	private InetAddress ReaderIP;
	
	public Reader()
	{
		this.ReaderIP=null;
	}
	
	public InetAddress getAddress()
	{
		return this.ReaderIP;
	}
	
	public void sedAddress(InetAddress newIP)
	{
		this.ReaderIP=newIP;
	}
}
