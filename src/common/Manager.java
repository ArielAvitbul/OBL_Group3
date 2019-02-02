package common;
/**
 * This class represents a Manager in the system
 * @author Good Guy
 *
 */
public class Manager extends Librarian {
	public Manager(int userID, String username, String password, int workerNum, int permissionLevel) {
		super(userID, username, password, workerNum, permissionLevel,Member.Status.ACTIVE);
	}
}
