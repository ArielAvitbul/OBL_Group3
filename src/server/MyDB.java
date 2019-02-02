package server;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;
/**
 * Handles database connection and data manipulation
 * @author Ariel
 *
 */
public class MyDB {
	private final String IP= "localhost";
	private final String port="3306";
	private final String schema="oblg3";
	private Connection conn;
	/**
	 * Builds a MyDB instance
	 * @param user - SQL User
	 * @param pass - SQL password
	 */
	public MyDB(String user, String pass) {
		try {
			   Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://"+IP+":"+port+"/"+schema,user,pass);
		if (conn!=null)
		System.out.println("SQL Succesfully connected.");
		else
			System.out.println("SQL didn't connect.");
		} catch (ClassNotFoundException e) {System.out.println("Something went wrong with the driver...");}
	catch (SQLException e) {e.printStackTrace();System.exit(1);}
	}
	/**
	 * used for 'select' view
	 * @author Ariel
	 * @param query
	 * @return ResultSet
	 * @throws SQLException
	 */
	public ResultSet select(String query) throws SQLException {
		return conn.createStatement().executeQuery(query);
	}
	/**
	 * used for 'insert' view, also executes it
	 * @author Ariel
	 * @param query
	 * @return success/fail (1/0)
	 * @throws SQLException
	 */
	public int insertWithExecute(String query) throws SQLException {
		return conn.createStatement().executeUpdate(query);
	}
	/**
	 * used for 'update' view
	 * @author Ariel
	 * @param query
	 * @return PreparedStatement
	 * @throws SQLException
	 */
	public PreparedStatement update(String query) throws SQLException {
			return conn.prepareStatement(query);
	}
	/**
	 * used for 'update' view, also executes it
	 * @author Ariel
	 * @param query
	 * @return
	 * @throws SQLException
	 */
	public int updateWithExecute(String query) throws SQLException {
		return conn.prepareStatement(query).executeUpdate();
}
	/**
	 * Moves the cursor to the first row inthis ResultSet object.
	 * @author Ariel
	 * @param rs
	 * @return true if the cursor is on a valid row; false if there are no rows in the result set
	 * @throws SQLException
	 */
	public boolean hasResults(ResultSet rs) throws SQLException {
		return rs.first();
	}
	/**
	 * finalizes the instance, close connection to DB
	 */
	@Override
	protected void finalize() throws Throwable {
	conn.close();
	System.out.println("Database finalized.");
	super.finalize();
	}	
}
