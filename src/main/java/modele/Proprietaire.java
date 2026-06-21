package modele;

/**
 * Modèle représentant le propriétaire d'un véhicule.
 */
public class Proprietaire {

    private int id;
    private String nom;
    private String prenom;
    private String telephone;
    private String typeProprietaire; // Particulier / Professionnel

    public Proprietaire() {
    }

    public Proprietaire(int id, String nom, String prenom, String telephone, String typeProprietaire) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.typeProprietaire = typeProprietaire;
    }

    public Proprietaire(String nom, String prenom, String telephone, String typeProprietaire) {
        this(0, nom, prenom, telephone, typeProprietaire);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getTypeProprietaire() {
        return typeProprietaire;
    }

    public void setTypeProprietaire(String typeProprietaire) {
        this.typeProprietaire = typeProprietaire;
    }

    @Override
    public String toString() {
        return prenom + " " + nom;
    }
}
