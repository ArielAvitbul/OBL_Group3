package common;

import java.io.Serializable;
import java.util.HashMap;
/**
 * This class represents a Data transfered between client and server.
 * contains an HashMap with the relevant objects.
 * @author Ariel
 *
 */
public class MyData implements Serializable{
	private String action;
	private HashMap<String,Object> data;
	public MyData(String datatype) {
		this.action=datatype;
		this.data = new HashMap<>();
	}
	public MyData() {
		this.data = new HashMap<>();
	}
	public MyData(String datatype, String what, Object item) {
		this.action=datatype;
		this.data = new HashMap<>();
		add(what,item);
	}
	
	public HashMap<String,Object> getData() {
		return data;
	}
	public void add(String datatype, Object data) {
		getData().put(datatype, data);
	}
	public Object getData(String datatype) {
		return getData().containsKey(datatype) ? getData().get(datatype) : 0;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getAction() {
		return action;
	}
}
