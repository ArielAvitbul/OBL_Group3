package server;

import java.sql.ResultSet;
import java.sql.SQLException;


import application.MyData;

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
	public MyData login (MyDB db , MyData data) throws SQLException{
		String MyQuery = "SELECT userName,password FROM Members WHERE userName="+data.getData("id")+"AND password="+data.getData("password");
		ResultSet memberMatch = db.select(MyQuery);
		if(isReultSetEmpty(memberMatch))
			return new MyData("No such member");
		data.setAction("login approved");
		return data;
	}
	private boolean isReultSetEmpty(ResultSet memberMatch) throws SQLException {
		return !memberMatch.first();
	}

}
