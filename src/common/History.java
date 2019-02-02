package common;

import java.io.Serializable;
import java.sql.Timestamp;
/**
 * This class represents all of the member's history of activities in the system
 * @author Good Guy
 *
 */
public class History implements Serializable {

	private String name;
	private final String type;
	private Timestamp actualDate;
	
	public History( String type,String name, Timestamp actualDate) {
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

	public Timestamp getActualDate() {
		return actualDate;
	}

	public void setActualDate(Timestamp actualDate) {
		this.actualDate = actualDate;
	}

	public String getType() {
		return type;
	}
	public String toString() {
		return ("type= "+ this.type+" name= "+this.name+" date= "+this.actualDate);
		
	}
	}
	