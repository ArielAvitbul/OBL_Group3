package common;

import java.io.Serializable;
import java.util.Date;

public class History implements Serializable {

	private String name;
	private final String type;
	private Date actualDate;
	
	public History( String type,String name, Date actualDate) {
		super();
		this.type = type;
		this.actualDate = actualDate;
		this.name=name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getActualDate() {
		return actualDate;
	}

	public void setActualDate(Date actualDate) {
		this.actualDate = actualDate;
	}

	public String getType() {
		return type;
	}
	public String toString() {
		return ("type= "+ this.type+" name= "+this.name+" date= "+this.actualDate);
		
	}
	}
	