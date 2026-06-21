package modele;

import java.time.LocalDate;

/**
 * Modèle représentant un lavage (2ème entité principale, liée à Vehicule).
 */
public class Lavage {

    private int id;
    private Vehicule vehicule;       // lien vers la 1ère entité
    private String typeLavage;       // Basic / Premium / Complet
    private LocalDate dateLavage;
    private int duree;               // en minutes
    private double prix;
    private String etat;             // En attente / En cours / Terminé
    private boolean paye;
    private String employe;
    private String remarques;

    public Lavage() {
    }

    public Lavage(int id, Vehicule vehicule, String typeLavage, LocalDate dateLavage, int duree,
                   double prix, String etat, boolean paye, String employe, String remarques) {
        this.id = id;
        this.vehicule = vehicule;
        this.typeLavage = typeLavage;
        this.dateLavage = dateLavage;
        this.duree = duree;
        this.prix = prix;
        this.etat = etat;
        this.paye = paye;
        this.employe = employe;
        this.remarques = remarques;
    }

    public Lavage(Vehicule vehicule, String typeLavage, LocalDate dateLavage, int duree,
                   double prix, String etat, boolean paye, String employe, String remarques) {
        this(0, vehicule, typeLavage, dateLavage, duree, prix, etat, paye, employe, remarques);
    }

    // ----- Getters / Setters -----

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Vehicule getVehicule() {
        return vehicule;
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }

    public String getTypeLavage() {
        return typeLavage;
    }

    public void setTypeLavage(String typeLavage) {
        this.typeLavage = typeLavage;
    }

    public LocalDate getDateLavage() {
        return dateLavage;
    }

    public void setDateLavage(LocalDate dateLavage) {
        this.dateLavage = dateLavage;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public boolean isPaye() {
        return paye;
    }

    public void setPaye(boolean paye) {
        this.paye = paye;
    }

    public String getEmploye() {
        return employe;
    }

    public void setEmploye(String employe) {
        this.employe = employe;
    }

    public String getRemarques() {
        return remarques;
    }

    public void setRemarques(String remarques) {
        this.remarques = remarques;
    }
}
