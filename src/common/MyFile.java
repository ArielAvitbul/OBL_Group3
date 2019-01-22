package common;

import java.io.Serializable;

public class MyFile implements Serializable {
	
	private String Description=null;
	private String fileName=null;	
	public  byte[] mybytearray;
	private String writeToPath = null;
	
	
	public void initArray(int size)
	{
		mybytearray = new byte [size];	
	}
	
	public MyFile(Book book) {
		this.fileName = String.valueOf(book.getBookID());
	}
	
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public int getSize() {
		return mybytearray.length;
	}

	public byte[] getMybytearray() {
		return mybytearray;
	}
	
	public byte getMybytearray(int i) {
		return mybytearray[i];
	}

	public void setMybytearray(byte[] mybytearray) {
		
		for(int i=0;i<mybytearray.length;i++)
		this.mybytearray[i] = mybytearray[i];
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getWriteToPath() {
		return writeToPath;
	}

	public void setWriteToPath(String writeToPath) {
		this.writeToPath = writeToPath;
	}	
	
}