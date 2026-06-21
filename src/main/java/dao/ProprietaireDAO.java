package dao;

import modele.Proprietaire;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsable des opérations SQL liées à la table "proprietaire".
 */
public class ProprietaireDAO {

    /**
     * Ajoute un nouveau propriétaire et retourne son id généré.
     */
    public int ajouter(Proprietaire p) {
        String sql = "INSERT INTO proprietaire (nom, prenom, telephone, type_proprietaire) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = DatabaseConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, p.getNom());
            stmt.setString(2, p.getPrenom());
            stmt.setString(3, p.getTelephone());
            stmt.setString(4, p.getTypeProprietaire());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Modifie un propriétaire existant.
     */
    public boolean modifier(Proprietaire p) {
        String sql = "UPDATE proprietaire SET nom=?, prenom=?, telephone=?, type_proprietaire=? WHERE id=?";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, p.getNom());
            stmt.setString(2, p.getPrenom());
            stmt.setString(3, p.getTelephone());
            stmt.setString(4, p.getTypeProprietaire());
            stmt.setInt(5, p.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retourne tous les propriétaires.
     */
    public List<Proprietaire> getTous() {
        List<Proprietaire> liste = new ArrayList<>();
        String sql = "SELECT * FROM proprietaire ORDER BY id DESC";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                liste.add(mapResultSetToProprietaire(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Retourne un propriétaire à partir de son id.
     */
    public Proprietaire getParId(int id) {
        String sql = "SELECT * FROM proprietaire WHERE id = ?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProprietaire(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Proprietaire mapResultSetToProprietaire(ResultSet rs) throws SQLException {
        Proprietaire p = new Proprietaire();
        p.setId(rs.getInt("id"));
        p.setNom(rs.getString("nom"));
        p.setPrenom(rs.getString("prenom"));
        p.setTelephone(rs.getString("telephone"));
        p.setTypeProprietaire(rs.getString("type_proprietaire"));
        return p;
    }
}
