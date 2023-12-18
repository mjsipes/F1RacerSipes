
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLExecutor {

    private static final String DB_URL = "jdbc:mysql://localhost/";
    private static final String USER = "root";
    private static final String PASS = "";

    public static void main(String[] args) {
        executeSQLScript("f1racer.sql");
    }

    private static void executeSQLScript(String filePath) {
    	 System.out.print( "SQLExecutor: executeSQLScript " + filePath );
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement statement = conn.createStatement();
             BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line;
            StringBuilder sqlQuery = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                // Skip comments and empty lines
                if (line.startsWith("--") || line.trim().isEmpty()) {
                    continue;
                }
                sqlQuery.append(line);

                // If one command complete
                if (line.endsWith(";")) {
                    statement.execute(sqlQuery.toString());
                    sqlQuery = new StringBuilder();
                }
            }

            System.out.println("SQL script executed successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL Error: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("File Read Error: " + e.getMessage());
        }
    }
}
