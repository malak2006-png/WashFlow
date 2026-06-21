package controleur;

import application.MainApp;
import dao.LavageDAO;
import dao.VehiculeDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import modele.Lavage;
import modele.Vehicule;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contrôleur de l'interface Gestion des Lavages (GestionLavage.fxml).
 * Relie l'interface graphique au LavageDAO et au VehiculeDAO (pour le lien entre entités).
 */
public class GestionLavageController implements Initializable {

    // ----- Champs du formulaire -----
    @FXML private ComboBox<Vehicule> vehiculeComboBox;
    @FXML private ComboBox<String> typeLavageComboBox;
    @FXML private DatePicker dateLavagePicker;
    @FXML private Slider dureeSlider;
    @FXML private Label dureeValueLabel;
    @FXML private TextField prixField;
    @FXML private CheckBox payeCheckBox;
    @FXML private ComboBox<String> etatComboBox;
    @FXML private TextField employeField;
    @FXML private TextArea remarquesArea;
    @FXML private Label messageLabel;

    // ----- Recherche / filtrage -----
    @FXML private TextField rechercheField;
    @FXML private ComboBox<String> filtreEtatComboBox;
    @FXML private Label resultatsLabel;

    // ----- Tableau -----
    @FXML private TableView<Lavage> lavageTable;
    @FXML private TableColumn<Lavage, Integer> colId;
    @FXML private TableColumn<Lavage, String> colVehicule;
    @FXML private TableColumn<Lavage, String> colType;
    @FXML private TableColumn<Lavage, String> colDate;
    @FXML private TableColumn<Lavage, String> colDuree;
    @FXML private TableColumn<Lavage, String> colPrix;
    @FXML private TableColumn<Lavage, String> colEtat;
    @FXML private TableColumn<Lavage, String> colPaye;

    // ----- Indicateurs en bas -----
    @FXML private Label totalLavagesLabel;
    @FXML private Label termineLabel;
    @FXML private Label enCoursLabel;
    @FXML private Label revenuLabel;

    private final LavageDAO lavageDAO = new LavageDAO();
    private final VehiculeDAO vehiculeDAO = new VehiculeDAO();
    private Lavage lavageSelectionne;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ComboBox véhicules (toString() de Vehicule affiche "immat - marque modele")
        vehiculeComboBox.setItems(FXCollections.observableArrayList(vehiculeDAO.getTous()));

        typeLavageComboBox.setItems(FXCollections.observableArrayList("Basic", "Premium", "Complet"));
        etatComboBox.setItems(FXCollections.observableArrayList("En attente", "En cours", "Terminé"));
        etatComboBox.setValue("En attente");

        filtreEtatComboBox.setItems(FXCollections.observableArrayList("Tous", "En attente", "En cours", "Terminé"));
        filtreEtatComboBox.setValue("Tous");

        dureeSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                dureeValueLabel.setText(newVal.intValue() + " min"));

        // Colonnes du tableau
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colVehicule.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getVehicule().getImmatriculation()));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeLavage"));
        colDate.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDateLavage().toString()));
        colDuree.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDuree() + " min"));
        colPrix.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPrix() + " MAD"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));
        colPaye.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().isPaye() ? "Oui" : "Non"));

        lavageTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                remplirFormulaire(newVal);
                lavageSelectionne = newVal;
            }
        });

        rechercheField.textProperty().addListener((obs, oldVal, newVal) -> rechercher());
        filtreEtatComboBox.valueProperty().addListener((obs, oldVal, newVal) -> rechercher());

        chargerDonnees();
    }

    private void chargerDonnees() {
        List<Lavage> liste = lavageDAO.getTous();
        lavageTable.setItems(FXCollections.observableArrayList(liste));
        mettreAJourIndicateurs(liste);
    }

    private void rechercher() {
        String motCle = rechercheField.getText() == null ? "" : rechercheField.getText();
        String etat = filtreEtatComboBox.getValue();
        List<Lavage> resultats = lavageDAO.rechercher(motCle, etat);
        lavageTable.setItems(FXCollections.observableArrayList(resultats));
        mettreAJourIndicateurs(resultats);
    }

    private void mettreAJourIndicateurs(List<Lavage> liste) {
        long termines = liste.stream().filter(l -> l.getEtat().equals("Terminé")).count();
        long enCours = liste.stream().filter(l -> l.getEtat().equals("En cours")).count();
        double revenu = liste.stream().filter(Lavage::isPaye).mapToDouble(Lavage::getPrix).sum();

        resultatsLabel.setText(liste.size() + " résultats");
        totalLavagesLabel.setText("Total lavages: " + liste.size());
        termineLabel.setText("Terminés: " + termines);
        enCoursLabel.setText("En cours: " + enCours);
        revenuLabel.setText("Revenus: " + revenu + " MAD");
    }

    @FXML
    private void handleAjouter(ActionEvent event) {
        if (!validerFormulaire()) return;

        Lavage l = construireLavageDepuisFormulaire();
        boolean succes = lavageDAO.ajouter(l);

        if (succes) {
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Lavage ajouté avec succès !");
            viderFormulaire();
            chargerDonnees();
        } else {
            messageLabel.setText("Erreur lors de l'ajout du lavage.");
        }
    }

    @FXML
    private void handleModifier(ActionEvent event) {
        if (lavageSelectionne == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un lavage à modifier.");
            return;
        }
        if (!validerFormulaire()) return;

        Lavage l = construireLavageDepuisFormulaire();
        l.setId(lavageSelectionne.getId());

        boolean succes = lavageDAO.modifier(l);
        if (succes) {
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Lavage modifié avec succès !");
            viderFormulaire();
            chargerDonnees();
        } else {
            messageLabel.setText("Erreur lors de la modification du lavage.");
        }
    }

    @FXML
    private void handleSupprimer(ActionEvent event) {
        if (lavageSelectionne == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un lavage à supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Voulez-vous vraiment supprimer ce lavage ?");

        confirmation.showAndWait().ifPresent(reponse -> {
            if (reponse == ButtonType.OK) {
                boolean succes = lavageDAO.supprimer(lavageSelectionne.getId());
                if (succes) {
                    afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Lavage supprimé.");
                    viderFormulaire();
                    chargerDonnees();
                } else {
                    afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le lavage.");
                }
            }
        });
    }

    @FXML
    private void handleActualiser(ActionEvent event) {
        rechercheField.clear();
        filtreEtatComboBox.setValue("Tous");
        vehiculeComboBox.setItems(FXCollections.observableArrayList(vehiculeDAO.getTous()));
        chargerDonnees();
    }

    // ----- Navigation (MenuBar) -----
    @FXML private void handleAllerVehicules(ActionEvent event) { MainApp.changerScene("vue/GestionVehicule.fxml"); }
    @FXML private void handleAllerLavages(ActionEvent event) { MainApp.changerScene("vue/GestionLavage.fxml"); }
    @FXML private void handleAllerDashboard(ActionEvent event) { MainApp.changerScene("vue/Dashboard.fxml"); }
    @FXML private void handleQuitter(ActionEvent event) { System.exit(0); }
    @FXML private void handleAPropos(ActionEvent event) { utils.AProposDialog.afficher(); }

    // ----- Méthodes utilitaires privées -----

    private boolean validerFormulaire() {
        if (vehiculeComboBox.getValue() == null || typeLavageComboBox.getValue() == null
                || dateLavagePicker.getValue() == null || prixField.getText().isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs obligatoires");
            return false;
        }
        try {
            Double.parseDouble(prixField.getText());
        } catch (NumberFormatException e) {
            messageLabel.setText("Le prix doit être un nombre valide.");
            return false;
        }
        messageLabel.setText("");
        return true;
    }

    private Lavage construireLavageDepuisFormulaire() {
        Lavage l = new Lavage();
        l.setVehicule(vehiculeComboBox.getValue());
        l.setTypeLavage(typeLavageComboBox.getValue());
        l.setDateLavage(dateLavagePicker.getValue());
        l.setDuree((int) dureeSlider.getValue());
        l.setPrix(Double.parseDouble(prixField.getText()));
        l.setEtat(etatComboBox.getValue());
        l.setPaye(payeCheckBox.isSelected());
        l.setEmploye(employeField.getText());
        l.setRemarques(remarquesArea.getText());
        return l;
    }

    private void remplirFormulaire(Lavage l) {
        vehiculeComboBox.setValue(l.getVehicule());
        typeLavageComboBox.setValue(l.getTypeLavage());
        dateLavagePicker.setValue(l.getDateLavage());
        dureeSlider.setValue(l.getDuree());
        prixField.setText(String.valueOf(l.getPrix()));
        payeCheckBox.setSelected(l.isPaye());
        etatComboBox.setValue(l.getEtat());
        employeField.setText(l.getEmploye());
        remarquesArea.setText(l.getRemarques());
    }

    private void viderFormulaire() {
        vehiculeComboBox.setValue(null);
        typeLavageComboBox.setValue(null);
        dateLavagePicker.setValue(LocalDate.now());
        dureeSlider.setValue(30);
        prixField.clear();
        payeCheckBox.setSelected(false);
        etatComboBox.setValue("En attente");
        employeField.clear();
        remarquesArea.clear();
        lavageSelectionne = null;
        lavageTable.getSelectionModel().clearSelection();
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
