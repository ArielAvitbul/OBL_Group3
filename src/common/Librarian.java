package common;

public class Librarian extends Member{

	public enum levels
	{
		REGULAR,
		SPECIAL
	}
	private final int librarianNumber;
	private final int employeeID;
	private levels permissionLevel;
	
	public Librarian(int newLibrarianNumber, int newEmployeeID, levels newPermissionLevel)
	{
		super();
		this.librarianNumber=newLibrarianNumber;
		this.employeeID=newEmployeeID;
		this.permissionLevel=newPermissionLevel;
	}
	
	public int getLibrarianNumber() 
	{
		return librarianNumber;
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
	
}
