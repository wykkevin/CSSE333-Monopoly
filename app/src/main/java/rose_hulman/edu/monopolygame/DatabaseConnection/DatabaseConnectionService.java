package rose_hulman.edu.monopolygame.DatabaseConnection;

/**
 * Created by Hao Yang on 1/23/2018.
 */

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionService {

    private String url = "jdbc:jtds:sqlserver://${dbServer};databaseName=${dbName};user=${user};password=${pass};";

    private Connection connection = null;

    private String databaseName;
    private String serverName;
    private volatile static DatabaseConnectionService service;

    private DatabaseConnectionService(String serverName, String databaseName) {
        Log.d("DBCONNECT", "Created Connection Service");
        this.serverName = serverName;
        this.databaseName = databaseName;
    }

    public boolean connect(String user, String pass) {
        String connectionString = url.replace("${dbServer}", serverName).replace("${dbName}", databaseName).replace("${user}", user).replace("${pass}", pass);
        Log.d("DBCONNECT", connectionString);
        try {
//            SQLServerDriver sqlServerDriver = new SQLServerDriver();
//            Properties properties = new Properties();
//            connection = sqlServerDriver.connect(url, new Properties());
//            Log.d("DBCONNECT", "DB connection successful");
          Class.forName("net.sourceforge.jtds.jdbc.Driver");
          connection = DriverManager.getConnection(connectionString);
            return true;
        } catch (Exception e) {
            Log.d("DBCONNECT", "DB connection failed because of " + e.toString());
            return false;
        }
    }

    public static DatabaseConnectionService getInstance(String serverName, String databaseName) {
        if (service == null) {
            synchronized (DatabaseConnectionService.class) {
                if (service == null) {
                    service = new DatabaseConnectionService(serverName, databaseName);
                }
            }
        }
        return service;
    }


    public Connection getConnection() {
        return this.connection;
    }

    public void closeConnection() {
        try {
            connection.close();
            connection = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

