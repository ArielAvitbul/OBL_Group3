package common;
/**
 * This class represents a Librarian in the system
 * @author Good Guy
 *@see Member
 */
public class Librarian extends Member{

	public enum levels
	{
		REGULAR,
		SPECIAL
	}
	private final int workerNum;
	private final int employeeID;
	private levels permissionLevel;
	
	public Librarian(int userID,String username,String password,int workerNum, int permissionLevel, Member.Status status)
	{
		super(userID,username,password,status);
		this.workerNum=workerNum;
		this.employeeID=userID;
		this.permissionLevel=(permissionLevel==0 ? levels.REGULAR : levels.SPECIAL);
	}
	
	public int getEmployeeID() 
	{
		return employeeID;
	}
	
	public levels getPermissionLevel() 
	{
		return permissionLevel;
	}
	
	public void setPermissionLevel(levels permissionLevel) 
	{
		this.permissionLevel = permissionLevel;
	}

	public int getWorkerNum() {
		return workerNum;
	}
	
}
