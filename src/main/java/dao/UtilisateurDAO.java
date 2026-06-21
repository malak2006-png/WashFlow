package dao;

import modele.Utilisateur;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO responsable de la vérification des identifiants de connexion
 * (table "utilisateur"), utilisé par LoginController.
 */
public class UtilisateurDAO {

    /**
     * Vérifie si le couple username/password correspond à un utilisateur existant.
     * Retourne l'objet Utilisateur si trouvé, sinon null.
     */
    public Utilisateur authentifier(String username, String password) {
        String sql = "SELECT * FROM utilisateur WHERE username = ? AND password = ?";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Utilisateur u = new Utilisateur();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password"));
                    u.setRole(rs.getString("role"));
                    return u;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // aucun utilisateur correspondant trouvé
    }
}
