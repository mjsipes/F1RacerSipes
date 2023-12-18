import java.sql.*;

import gsonClasses.*;


public class jdbc {

	private static Connection conn = null;
	private static PreparedStatement preparedStatement = null;
	private static Statement statement = null;
	private static ResultSet resultSet = null;
	private static String password = "";//MYSQL password


	public static boolean login(String userNameInput, String userPassword) {
    	System.out.println("jdbc: login for " +userNameInput + userPassword);
	    String query = "SELECT * FROM Accounts WHERE UserName = ? AND UserPassword = ?";

	    try {
	        // Load and register the MySQL JDBC driver
	        try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        // Establish database connection
	        conn = DriverManager.getConnection("jdbc:mysql://localhost/f1racer?user=root&password=" + password);
	        preparedStatement = conn.prepareStatement(query);
	        preparedStatement.setString(1, userNameInput);
	        preparedStatement.setString(2, userPassword);
	        resultSet = preparedStatement.executeQuery();

	        if (resultSet.next()) {
	            return true;
	        } else {
	            return false;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false; // Return false in case of exception
	    } finally {
	        // Close resources
	        try {
	            if (resultSet != null) resultSet.close();
	            if (preparedStatement != null) preparedStatement.close();
	            if (conn != null) conn.close();
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    }
	}
	
	public static PlayerStats getPlayerStats(String userNameInput) {
    	System.out.println("jdbc: getPlayerStats for " +userNameInput);
	    String query = "SELECT * FROM Accounts WHERE UserName = ?";
	    PlayerStats stats = new PlayerStats();

		try {
	        // Load and register the MySQL JDBC driver
	        try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        // Establish database connection
	        conn = DriverManager.getConnection("jdbc:mysql://localhost/f1racer?user=root&password=" + password);
	        preparedStatement = conn.prepareStatement(query);
	        preparedStatement.setString(1, userNameInput);
	        resultSet = preparedStatement.executeQuery();

	        if (resultSet.next()) {
	            stats.userName = resultSet.getString("UserName");
	            stats.gamesPlayed = resultSet.getInt("gamesPlayed");
	            stats.gamesWon = resultSet.getInt("gamesWon");
	            stats.totalCharactersTyped = resultSet.getInt("totalCharactersTyped");
	            stats.totalWordsTyped = resultSet.getInt("totalWordsTyped");
	            stats.bestWPM = resultSet.getInt("bestWordsPerMinute");
	            return stats;
	        } else {
	            // Login fail
	            return null;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null; // Return false in case of exception
	    } finally {
	        // Close resources
	        try {
	            if (resultSet != null) resultSet.close();
	            if (preparedStatement != null) preparedStatement.close();
	            if (conn != null) conn.close();
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    }
	}
	
	public static boolean updatePlayerStats(PlayerStats stats) {
    	System.out.println("jdbc: updatePlayerStats");

	    String updateQuery = "UPDATE Accounts SET gamesPlayed = ?, gamesWon = ?, totalCharactersTyped = ?, totalWordsTyped = ?, bestWordsPerMinute = ? WHERE UserName = ?";
	    String insertQuery = "INSERT INTO Accounts (UserName, gamesPlayed, gamesWon, totalCharactersTyped, totalWordsTyped, bestWordsPerMinute) VALUES (?, ?, ?, ?, ?, ?)";
	    Connection conn = null;
	    PreparedStatement preparedStatement = null;

	    try {
	        // Load and register the MySQL JDBC driver
	        Class.forName("com.mysql.cj.jdbc.Driver");

	        // Establish database connection
	        conn = DriverManager.getConnection("jdbc:mysql://localhost/f1racer?user=root&password=" + password);

	        // Prepare the update statement
	        preparedStatement = conn.prepareStatement(updateQuery);
	        preparedStatement.setInt(1, stats.gamesPlayed);
	        preparedStatement.setInt(2, stats.gamesWon);
	        preparedStatement.setInt(3, stats.totalCharactersTyped);
	        preparedStatement.setInt(4, stats.totalWordsTyped);
	        preparedStatement.setInt(5, stats.bestWPM);
	        preparedStatement.setString(6, stats.userName);

	        int rowsAffected = preparedStatement.executeUpdate();

	        return true;
	    } catch (ClassNotFoundException | SQLException e) {
	        e.printStackTrace();
	        return false;
	    } finally {
	        // Close resources
	        try {
	            if (preparedStatement != null) preparedStatement.close();
	            if (conn != null) conn.close();
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    }
	}


	public static int signUp(String username, String userPassword) {
    	System.out.println("jdbc: signUp for " +username + userPassword);
	    String queryCheck = "SELECT * FROM Accounts WHERE UserName = ?";
	    String queryInsert = "INSERT INTO Accounts (UserName, UserPassword) VALUES (?, ?)";
	    PreparedStatement preparedStatementCheck = null;
	    PreparedStatement preparedStatementInsert = null;
	    try {
	        conn = DriverManager.getConnection("jdbc:mysql://localhost/f1racer?user=root&password=" + password);

	        preparedStatementCheck = conn.prepareStatement(queryCheck);
	        preparedStatementCheck.setString(1, username);
	        resultSet = preparedStatementCheck.executeQuery();

	        if (resultSet.next()) {
	            return 1; // Username already exists
	        } else {
	            preparedStatementInsert = conn.prepareStatement(queryInsert);
	            preparedStatementInsert.setString(1, username);
	            preparedStatementInsert.setString(2, userPassword);
	            int rowsAffected = preparedStatementInsert.executeUpdate();
	            return (rowsAffected > 0) ? 0 : -1; // 0 for success, -1 for failure
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return -1; // -1 for failure due to SQL exception
	    } finally {
	        // Close resources
	        try {
	            if (resultSet != null) resultSet.close();
	            if (preparedStatementCheck != null) preparedStatementCheck.close();
	            if (preparedStatementInsert != null) preparedStatementInsert.close();
	            if (conn != null) conn.close();
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    }
	}


}
