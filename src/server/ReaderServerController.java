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
}
