package server;

import java.sql.ResultSet;
import java.sql.SQLException;


import application.MyData;
import common.Member;

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
	public MyData login (MyDB db , MyData data) throws SQLException{
		String MyQuery = "SELECT * FROM Members WHERE userName="+data.getData("id")+"AND password="+data.getData("password");
		ResultSet memberMatch = db.select(MyQuery);
		if(!db.hasResults(memberMatch))
			return new MyData("No such member");
		data.getData().clear();
		for(int i = 0; i<memberMatch.getMetaData().getColumnCount();i++) {
			
		}
		data.add("MemberLoggedIn",null);
		data.setAction("login approved");
		return data;
	}
}
