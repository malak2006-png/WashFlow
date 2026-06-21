package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe unique responsable de la connexion JDBC à la base MySQL.
 * Toutes les classes DAO utilisent cette classe pour obtenir une connexion.
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/station_lavage?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // à adapter selon votre config MySQL/WAMP

    private static Connection connection;

    private DatabaseConnection() {
    }

    /**
     * Retourne une connexion unique (singleton) vers la base de données.
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Ferme la connexion (à appeler à la fermeture de l'application).
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
