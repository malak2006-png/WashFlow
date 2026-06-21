package dao;

import modele.Lavage;
import modele.Vehicule;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsable de toutes les opérations SQL liées à la table "lavage".
 */
public class LavageDAO {

    private final VehiculeDAO vehiculeDAO = new VehiculeDAO();

    /**
     * Ajoute un nouveau lavage dans la base de données.
     */
    public boolean ajouter(Lavage l) {
        String sql = "INSERT INTO lavage (vehicule_id, type_lavage, date_lavage, duree, prix, etat, "
                + "paye, employe, remarques) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, l.getVehicule().getId());
            stmt.setString(2, l.getTypeLavage());
            stmt.setDate(3, Date.valueOf(l.getDateLavage()));
            stmt.setInt(4, l.getDuree());
            stmt.setDouble(5, l.getPrix());
            stmt.setString(6, l.getEtat());
            stmt.setBoolean(7, l.isPaye());
            stmt.setString(8, l.getEmploye());
            stmt.setString(9, l.getRemarques());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Modifie un lavage existant.
     */
    public boolean modifier(Lavage l) {
        String sql = "UPDATE lavage SET vehicule_id=?, type_lavage=?, date_lavage=?, duree=?, prix=?, "
                + "etat=?, paye=?, employe=?, remarques=? WHERE id=?";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, l.getVehicule().getId());
            stmt.setString(2, l.getTypeLavage());
            stmt.setDate(3, Date.valueOf(l.getDateLavage()));
            stmt.setInt(4, l.getDuree());
            stmt.setDouble(5, l.getPrix());
            stmt.setString(6, l.getEtat());
            stmt.setBoolean(7, l.isPaye());
            stmt.setString(8, l.getEmploye());
            stmt.setString(9, l.getRemarques());
            stmt.setInt(10, l.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Supprime un lavage à partir de son id.
     */
    public boolean supprimer(int id) {
        String sql = "DELETE FROM lavage WHERE id=?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retourne la liste de tous les lavages, avec le véhicule lié (JOIN).
     */
    public List<Lavage> getTous() {
        List<Lavage> liste = new ArrayList<>();
        String sql = "SELECT l.*, v.immatriculation, v.marque, v.modele FROM lavage l "
                + "JOIN vehicule v ON l.vehicule_id = v.id ORDER BY l.id DESC";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                liste.add(mapResultSetToLavage(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Recherche / filtre les lavages par immatriculation, type ou état.
     */
    public List<Lavage> rechercher(String motCle, String etatFiltre) {
        List<Lavage> liste = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT l.*, v.immatriculation, v.marque, v.modele FROM lavage l "
                        + "JOIN vehicule v ON l.vehicule_id = v.id "
                        + "WHERE (v.immatriculation LIKE ? OR l.type_lavage LIKE ?)");

        if (etatFiltre != null && !etatFiltre.equalsIgnoreCase("Tous")) {
            sql.append(" AND l.etat = ?");
        }
        sql.append(" ORDER BY l.id DESC");

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql.toString())) {
            stmt.setString(1, "%" + motCle + "%");
            stmt.setString(2, "%" + motCle + "%");
            if (etatFiltre != null && !etatFiltre.equalsIgnoreCase("Tous")) {
                stmt.setString(3, etatFiltre);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    liste.add(mapResultSetToLavage(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Retourne le nombre total de lavages (pour le Dashboard).
     */
    public int getTotalLavages() {
        String sql = "SELECT COUNT(*) AS total FROM lavage";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Retourne le revenu total (somme des prix des lavages payés).
     */
    public double getRevenuTotal() {
        String sql = "SELECT SUM(prix) AS total FROM lavage WHERE paye = TRUE";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Retourne le nombre de lavages effectués aujourd'hui.
     */
    public int getLavagesAujourdhui() {
        String sql = "SELECT COUNT(*) AS total FROM lavage WHERE date_lavage = CURDATE()";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Retourne la répartition des lavages par type (Basic / Premium / Complet).
     * Clé = type, Valeur = nombre de lavages.
     */
    public List<Object[]> getRepartitionParType() {
        List<Object[]> resultats = new ArrayList<>();
        String sql = "SELECT type_lavage, COUNT(*) AS total FROM lavage GROUP BY type_lavage";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                resultats.add(new Object[]{rs.getString("type_lavage"), rs.getInt("total")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultats;
    }

    /**
     * Transforme une ligne du ResultSet en objet Lavage (avec son véhicule lié).
     */
    private Lavage mapResultSetToLavage(ResultSet rs) throws SQLException {
        Vehicule v = new Vehicule();
        v.setId(rs.getInt("vehicule_id"));
        v.setImmatriculation(rs.getString("immatriculation"));
        v.setMarque(rs.getString("marque"));
        v.setModele(rs.getString("modele"));

        Lavage l = new Lavage();
        l.setId(rs.getInt("id"));
        l.setVehicule(v);
        l.setTypeLavage(rs.getString("type_lavage"));
        Date date = rs.getDate("date_lavage");
        l.setDateLavage(date != null ? date.toLocalDate() : LocalDate.now());
        l.setDuree(rs.getInt("duree"));
        l.setPrix(rs.getDouble("prix"));
        l.setEtat(rs.getString("etat"));
        l.setPaye(rs.getBoolean("paye"));
        l.setEmploye(rs.getString("employe"));
        l.setRemarques(rs.getString("remarques"));
        return l;
    }
}
