package server;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
	
	public ResultSet select(String query) {
		try {
		Statement stmt = conn.createStatement();
			return stmt.executeQuery(query);
		} catch (SQLException e) {
			System.out.println("Failed to SELECT from Database");
			e.printStackTrace();
		}
		return null;
	}
	
	public PreparedStatement update(String query) {
		 try {
			return conn.prepareStatement(query);
		} catch (SQLException e) {
			System.out.println("Failed to UPDATE Database");
			e.printStackTrace();
		}
		 return null;
	}
	 
}
