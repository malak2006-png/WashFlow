package utils;

import modele.Lavage;
import modele.Vehicule;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Classe utilitaire pour exporter les données en fichiers CSV.
 */
public class CsvExporter {

    /**
     * Exporte la liste des véhicules dans un fichier CSV.
     */
    public static boolean exporterVehicules(List<Vehicule> vehicules, String cheminFichier) {
        try (FileWriter writer = new FileWriter(cheminFichier)) {
            writer.append("ID,Immatriculation,Marque,Modele,Couleur,Type,Proprietaire,Telephone,Actif,Date,NombreLavages,Remise\n");

            for (Vehicule v : vehicules) {
                writer.append(String.valueOf(v.getId())).append(",");
                writer.append(v.getImmatriculation()).append(",");
                writer.append(v.getMarque()).append(",");
                writer.append(v.getModele() != null ? v.getModele() : "").append(",");
                writer.append(v.getCouleur() != null ? v.getCouleur() : "").append(",");
                writer.append(v.getTypeVehicule()).append(",");
                writer.append(v.getProprietaire() != null ? v.getProprietaire().toString() : "").append(",");
                writer.append(v.getProprietaire() != null && v.getProprietaire().getTelephone() != null
                        ? v.getProprietaire().getTelephone() : "").append(",");
                writer.append(v.isActif() ? "Oui" : "Non").append(",");
                writer.append(v.getDateEnregistrement().toString()).append(",");
                writer.append(String.valueOf(v.getNombreLavages())).append(",");
                writer.append(String.valueOf(v.getRemise())).append("\n");
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Exporte la liste des lavages dans un fichier CSV.
     */
    public static boolean exporterLavages(List<Lavage> lavages, String cheminFichier) {
        try (FileWriter writer = new FileWriter(cheminFichier)) {
            writer.append("ID,Immatriculation,Type,Date,Duree,Prix,Etat,Paye,Employe\n");

            for (Lavage l : lavages) {
                writer.append(String.valueOf(l.getId())).append(",");
                writer.append(l.getVehicule().getImmatriculation()).append(",");
                writer.append(l.getTypeLavage()).append(",");
                writer.append(l.getDateLavage().toString()).append(",");
                writer.append(String.valueOf(l.getDuree())).append(",");
                writer.append(String.valueOf(l.getPrix())).append(",");
                writer.append(l.getEtat()).append(",");
                writer.append(l.isPaye() ? "Oui" : "Non").append(",");
                writer.append(l.getEmploye() != null ? l.getEmploye() : "").append("\n");
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
