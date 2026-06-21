package dao;

import modele.Proprietaire;
import modele.Vehicule;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsable de toutes les opérations SQL liées à la table "vehicule".
 * Le véhicule est lié à un propriétaire (table "proprietaire") via proprietaire_id.
 */
public class VehiculeDAO {

    private final ProprietaireDAO proprietaireDAO = new ProprietaireDAO();

    /**
     * Ajoute un nouveau véhicule. Si le véhicule a un propriétaire sans id (nouveau),
     * celui-ci est d'abord créé en base.
     */
    public boolean ajouter(Vehicule v) {
        // Crée le propriétaire en base s'il n'existe pas encore
        if (v.getProprietaire() != null && v.getProprietaire().getId() == 0) {
            int idGenere = proprietaireDAO.ajouter(v.getProprietaire());
            if (idGenere == -1) {
                System.err.println("ERREUR : impossible de créer le propriétaire en base (id généré = -1).");
                return false;
            }
            v.getProprietaire().setId(idGenere);
        }

        String sql = "INSERT INTO vehicule (immatriculation, marque, modele, couleur, type_vehicule, "
                + "proprietaire_id, actif, date_enregistrement, nombre_lavages, remise, remarques) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, v.getImmatriculation());
            stmt.setString(2, v.getMarque());
            stmt.setString(3, v.getModele());
            stmt.setString(4, v.getCouleur());
            stmt.setString(5, v.getTypeVehicule());
            if (v.getProprietaire() != null && v.getProprietaire().getId() > 0) {
                stmt.setInt(6, v.getProprietaire().getId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            stmt.setBoolean(7, v.isActif());
            stmt.setDate(8, Date.valueOf(v.getDateEnregistrement()));
            stmt.setInt(9, v.getNombreLavages());
            stmt.setDouble(10, v.getRemise());
            stmt.setString(11, v.getRemarques());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("ERREUR SQL lors de l'ajout du véhicule : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Modifie un véhicule existant (et met à jour son propriétaire lié).
     */
    public boolean modifier(Vehicule v) {
        // Met à jour ou crée le propriétaire
        if (v.getProprietaire() != null) {
            if (v.getProprietaire().getId() == 0) {
                int idGenere = proprietaireDAO.ajouter(v.getProprietaire());
                v.getProprietaire().setId(idGenere);
            } else {
                proprietaireDAO.modifier(v.getProprietaire());
            }
        }

        String sql = "UPDATE vehicule SET immatriculation=?, marque=?, modele=?, couleur=?, "
                + "type_vehicule=?, proprietaire_id=?, actif=?, date_enregistrement=?, nombre_lavages=?, "
                + "remise=?, remarques=? WHERE id=?";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, v.getImmatriculation());
            stmt.setString(2, v.getMarque());
            stmt.setString(3, v.getModele());
            stmt.setString(4, v.getCouleur());
            stmt.setString(5, v.getTypeVehicule());
            if (v.getProprietaire() != null) {
                stmt.setInt(6, v.getProprietaire().getId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            stmt.setBoolean(7, v.isActif());
            stmt.setDate(8, Date.valueOf(v.getDateEnregistrement()));
            stmt.setInt(9, v.getNombreLavages());
            stmt.setDouble(10, v.getRemise());
            stmt.setString(11, v.getRemarques());
            stmt.setInt(12, v.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Supprime un véhicule à partir de son id.
     */
    public boolean supprimer(int id) {
        String sql = "DELETE FROM vehicule WHERE id=?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retourne la liste de tous les véhicules, avec leur propriétaire (JOIN).
     */
    public List<Vehicule> getTous() {
        List<Vehicule> liste = new ArrayList<>();
        String sql = "SELECT v.*, p.nom, p.prenom, p.telephone, p.type_proprietaire "
                + "FROM vehicule v LEFT JOIN proprietaire p ON v.proprietaire_id = p.id "
                + "ORDER BY v.id DESC";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                liste.add(mapResultSetToVehicule(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Recherche / filtre les véhicules par immatriculation, marque ou type.
     */
    public List<Vehicule> rechercher(String motCle, String typeFiltre) {
        List<Vehicule> liste = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT v.*, p.nom, p.prenom, p.telephone, p.type_proprietaire "
                        + "FROM vehicule v LEFT JOIN proprietaire p ON v.proprietaire_id = p.id "
                        + "WHERE (v.immatriculation LIKE ? OR v.marque LIKE ?)");

        if (typeFiltre != null && !typeFiltre.equalsIgnoreCase("Tous")) {
            sql.append(" AND v.type_vehicule = ?");
        }
        sql.append(" ORDER BY v.id DESC");

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql.toString())) {
            stmt.setString(1, "%" + motCle + "%");
            stmt.setString(2, "%" + motCle + "%");
            if (typeFiltre != null && !typeFiltre.equalsIgnoreCase("Tous")) {
                stmt.setString(3, typeFiltre);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    liste.add(mapResultSetToVehicule(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    /**
     * Retourne le nombre total de véhicules (pour le Dashboard).
     */
    public int getTotalVehicules() {
        String sql = "SELECT COUNT(*) AS total FROM vehicule";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Transforme une ligne du ResultSet en objet Vehicule (avec son Proprietaire lié).
     */
    private Vehicule mapResultSetToVehicule(ResultSet rs) throws SQLException {
        Vehicule v = new Vehicule();
        v.setId(rs.getInt("id"));
        v.setImmatriculation(rs.getString("immatriculation"));
        v.setMarque(rs.getString("marque"));
        v.setModele(rs.getString("modele"));
        v.setCouleur(rs.getString("couleur"));
        v.setTypeVehicule(rs.getString("type_vehicule"));
        v.setActif(rs.getBoolean("actif"));
        Date date = rs.getDate("date_enregistrement");
        v.setDateEnregistrement(date != null ? date.toLocalDate() : LocalDate.now());
        v.setNombreLavages(rs.getInt("nombre_lavages"));
        v.setRemise(rs.getDouble("remise"));
        v.setRemarques(rs.getString("remarques"));

        int proprietaireId = rs.getInt("proprietaire_id");
        if (!rs.wasNull()) {
            Proprietaire p = new Proprietaire();
            p.setId(proprietaireId);
            p.setNom(rs.getString("nom"));
            p.setPrenom(rs.getString("prenom"));
            p.setTelephone(rs.getString("telephone"));
            p.setTypeProprietaire(rs.getString("type_proprietaire"));
            v.setProprietaire(p);
        }

        return v;
    }
}
