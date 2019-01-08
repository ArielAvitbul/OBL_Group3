package common;

public class Librarian extends Member{

	public enum levels
	{
		REGULAR,
		SPECIAL
	}
	private final int workerNum;
	private final int employeeID;
	private levels permissionLevel;
	
	public Librarian(int userID,String username,String password, int workerNum, int permissionLevel)
	{
		super(userID,username,password);
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
