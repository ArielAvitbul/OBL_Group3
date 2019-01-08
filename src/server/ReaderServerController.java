package server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import client.MyData;
import common.Borrow;
import common.Librarian;
import common.Member;
import common.MemberCard;
import common.Violation;

public class ReaderServerController {
	public MyData search(MyDB db, MyData data) throws SQLException {
		MyData ret = new MyData("search");
		ResultSet rs = db.select("SELECT * FROM Books");
		while (!rs.isClosed() && rs.next()) {
				if (rs.getString("StudentID").equals(data.getData("student_id"))) {
					rs.close();
					return ret;
				}
			}
			return null;
	}
	/*method for confirming login request from client
	 * input: MyDB instance and MyData instance
	 * output: MyData (Member instance) that has been logged in or empty MyData
	 * Guy Wrote This
	 */
	public MyData login (MyDB db , MyData data) throws SQLException {
		String MyQuery = "SELECT *"
							+ "FROM users "
							+ "WHERE id = '"+ data.getData("id")+"' "
							+ "AND password = '"+data.getData("password")+"';";
		ResultSet memberMatch = db.select(MyQuery);
		data.getData().clear();
		if(!db.hasResults(memberMatch)) {
			data.setAction("login_failed");
			data.add("reason", "ID or password incorrect");
		} else if (memberMatch.getBoolean("loggedin")) {
			data.setAction("login_failed");
			data.add("reason", "Already logged in!");
		} else {
			data.setAction("login_approved");
			setLoggedIn(db,true,memberMatch.getInt("id"));
			data.add("MemberLoggedIn",createMember(memberMatch, db));
			}
		return data;
	}
	
	public void setLoggedIn(MyDB db, boolean value, String ip) throws SQLException { // TODO change name to id...
		PreparedStatement ps = db.update("UPDATE users SET loggedin =? WHERE ip =?");
		ps.setBoolean(1, value);
		ps.setString(2, ip);
		ps.executeUpdate();
	}
	
	public void setLoggedIn(MyDB db, boolean value, int id) throws SQLException { // TODO change name to id...
		PreparedStatement ps = db.update("UPDATE users SET loggedin =? WHERE id =?");
		ps.setBoolean(1, value);
		ps.setInt(2, id);
		ps.executeUpdate();
	}
	public void updateIP(MyDB db, String ip,int id) throws SQLException {
		PreparedStatement ps = db.update("UPDATE users SET ip=? WHERE id =?");
		ps.setString(1, ip);
		ps.setInt(2, id);
		ps.executeUpdate();
	}
	/*this method creates Member instance for the member that has logged in
	 * input: ResultSet (member details) and MyDB instance
	 * output: Member instance
	 */
	public Member createMember(ResultSet rs, MyDB db) throws SQLException {
			Member toReturn=null;
			if (rs.getInt("rank")==1) {
				ResultSet libRS = db.select("SELECT * FROM Librarian where id = "+ rs.getInt("id"));
				if (db.hasResults(libRS))
				toReturn = new Librarian(rs.getInt("id"), rs.getString("username"), rs.getString("password"), libRS.getInt("workerNum"),libRS.getInt("permissionLevel"));
			} else if (rs.getInt("rank")==0) {
			toReturn = new Member(rs.getInt("id"),rs.getString("name"),rs.getString("password"));
			}
			toReturn.setMemberCard(fetchMemberCard(rs.getInt("id"),db));
			return toReturn;
	}
	/* fetch membercard*/
	private MemberCard fetchMemberCard(int id, MyDB db) throws SQLException {
		ResultSet rs = db.select("SELECT * from member_card WHERE userID = "+ id);
		if (db.hasResults(rs))
		return new MemberCard(rs.getString("firstName"), rs.getString("lastName"), rs.getString("phoneNumber"), rs.getString("emailAddress"), null, null, rs.getInt("lateReturns"));
		return null;
	}
		/* function to get all of the specified member borrows
		 * input: memberID (unique) , MyDB instance.
		 * output: array of borrows.
		 */
	public Borrow[] getMemberBorrows(int memberID, MyDB db) throws SQLException {
		ArrayList<Borrow> memberBorrows = new ArrayList<Borrow>();
		String MyQuery = "SELECT * FROM Borrow WHERE memberID ="+Integer.toString(memberID);
		ResultSet rs = db.select(MyQuery);
		if(!db.hasResults(rs))
			return (Borrow[]) memberBorrows.toArray(); 
		while(rs.next()) {
			memberBorrows.add(new Borrow(rs.getInt(1),rs.getInt(2),rs.getDate(3),rs.getDate(4),rs.getDate(5),rs.getBoolean(6)));
		}
		return (Borrow[]) memberBorrows.toArray();
	}
	/* function to get all of the specified member violations
	 * input: memberID (unique) , MyDB instance.
	 * output: array of violations.
	 */
	public Violation[] getMemberViolations(int memberID, MyDB db) throws SQLException {
		ArrayList<Violation> memberViolations = new ArrayList<Violation>();
		String MyQuery = "SELECT * FROM Violation WHERE memberID ="+Integer.toString(memberID);
		ResultSet rs = db.select(MyQuery);
		if(!db.hasResults(rs))
			return (Violation[]) memberViolations.toArray(); 
		while(rs.next()) {
			memberViolations.add(new Violation(rs.getInt(1),rs.getDate(2),rs.getString(3),rs.getInt(4)));
		}
		return (Violation[]) memberViolations.toArray();
	}
}
