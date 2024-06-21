package searchengine.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class DBConnection {
    private static Connection connection;
    private static String dbName = "search_engine";
    @Value("${spring.datasource.username}")
    private static String dbUser;
    @Value("${spring.datasource.password}")
    private static String dbPass;

    public static Connection getConnection() throws SQLException {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + dbName +
                                "?user=" + dbUser + "&password=" + dbPass);
            } catch (SQLException e) {
                log.info(e.getMessage());
            }
        }
        return connection;
    }

//    public static void updatePage(int id, ) {
//
//    }


}
