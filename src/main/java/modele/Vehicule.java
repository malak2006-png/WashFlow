package modele;

import java.time.LocalDate;

/**
 * Modèle représentant un véhicule (1ère entité principale).
 */
public class Vehicule {

    private int id;
    private String immatriculation;
    private String marque;
    private String modele;
    private String couleur;
    private String typeVehicule;     // Berline / SUV / Camion / Moto
    private Proprietaire proprietaire; // lien vers le propriétaire (Nom, Prénom, Téléphone)
    private boolean actif;
    private LocalDate dateEnregistrement;
    private int nombreLavages;
    private double remise;           // en %
    private String remarques;

    public Vehicule() {
    }

    public Vehicule(int id, String immatriculation, String marque, String modele, String couleur,
                     String typeVehicule, Proprietaire proprietaire, boolean actif,
                     LocalDate dateEnregistrement, int nombreLavages, double remise, String remarques) {
        this.id = id;
        this.immatriculation = immatriculation;
        this.marque = marque;
        this.modele = modele;
        this.couleur = couleur;
        this.typeVehicule = typeVehicule;
        this.proprietaire = proprietaire;
        this.actif = actif;
        this.dateEnregistrement = dateEnregistrement;
        this.nombreLavages = nombreLavages;
        this.remise = remise;
        this.remarques = remarques;
    }

    // Constructeur sans id (pour un nouvel ajout)
    public Vehicule(String immatriculation, String marque, String modele, String couleur,
                     String typeVehicule, Proprietaire proprietaire, boolean actif,
                     LocalDate dateEnregistrement, int nombreLavages, double remise, String remarques) {
        this(0, immatriculation, marque, modele, couleur, typeVehicule, proprietaire, actif,
                dateEnregistrement, nombreLavages, remise, remarques);
    }

    // ----- Getters / Setters -----

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public String getTypeVehicule() {
        return typeVehicule;
    }

    public void setTypeVehicule(String typeVehicule) {
        this.typeVehicule = typeVehicule;
    }

    public Proprietaire getProprietaire() {
        return proprietaire;
    }

    public void setProprietaire(Proprietaire proprietaire) {
        this.proprietaire = proprietaire;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public LocalDate getDateEnregistrement() {
        return dateEnregistrement;
    }

    public void setDateEnregistrement(LocalDate dateEnregistrement) {
        this.dateEnregistrement = dateEnregistrement;
    }

    public int getNombreLavages() {
        return nombreLavages;
    }

    public void setNombreLavages(int nombreLavages) {
        this.nombreLavages = nombreLavages;
    }

    public double getRemise() {
        return remise;
    }

    public void setRemise(double remise) {
        this.remise = remise;
    }

    public String getRemarques() {
        return remarques;
    }

    public void setRemarques(String remarques) {
        this.remarques = remarques;
    }

    @Override
    public String toString() {
        // Utilisé notamment dans les ComboBox / ListView
        return immatriculation + " - " + marque + " " + (modele != null ? modele : "");
    }
}
