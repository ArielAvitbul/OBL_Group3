package server;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

public class MyDB {
	private final String IP= "localhost";
	private final String port="3306";
	private final String schema="oblg3";
	private Connection conn;
	public MyDB(String user, String pass) {
		try {
			   Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://"+IP+":"+port+"/"+schema,user,pass);
		System.out.println("SQL Succesfully connected.");
		} catch (ClassNotFoundException e) {System.out.println("Something went wrong with the driver...");}
	catch (SQLException e) {e.printStackTrace();System.exit(1);}
	}
	
	public ResultSet select(String query) throws SQLException {
		return conn.createStatement().executeQuery(query);
	}
	
	public int insertWithExecute(String query) throws SQLException {
		return conn.createStatement().executeUpdate(query);
	}
	
	public int insertAndGetKey(String query) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		stmt.executeUpdate();
		return stmt.getGeneratedKeys().getInt(1);
	}
	
	public PreparedStatement update(String query) throws SQLException {
			return conn.prepareStatement(query);
	}
	public int updateWithExecute(String query) throws SQLException {
		return conn.prepareStatement(query).executeUpdate();
}
	public boolean hasResults(ResultSet rs) throws SQLException {
		return rs.first();
	}
@Override
	protected void finalize() throws Throwable {
	conn.close();
	System.out.println("Database finalized.");
	super.finalize();
	}	
}
