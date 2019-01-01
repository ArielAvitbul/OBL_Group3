package common;

import java.net.InetAddress;

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
