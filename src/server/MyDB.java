package server;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import common.Book;

public class MyDB {
	private final String IP= "localhost";
	private final String port="3306";
	private final String schema="oblg3";
	private final String user="root";
	private final String pass="arielsql";
	private Connection conn;
	public MyDB() {
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
	
	public int insert(String query) throws SQLException {
		return conn.createStatement().executeUpdate(query);
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
}
