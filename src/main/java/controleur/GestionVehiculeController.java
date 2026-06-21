package controleur;

import application.MainApp;
import dao.VehiculeDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import modele.Proprietaire;
import modele.Vehicule;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Contrôleur de l'interface Gestion des Véhicules (GestionVehicule.fxml).
 * Relie l'interface graphique au VehiculeDAO (pattern MVC + DAO).
 */
public class GestionVehiculeController implements Initializable {

    // ----- Champs du formulaire : Véhicule -----
    @FXML private TextField immatriculationField;
    @FXML private TextField marqueField;
    @FXML private TextField modeleField;
    @FXML private TextField couleurField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private DatePicker dateEnregistrementPicker;
    @FXML private Spinner<Integer> nombreLavagesSpinner;
    @FXML private CheckBox actifCheckBox;
    @FXML private Slider remiseSlider;
    @FXML private Label remiseValueLabel;
    @FXML private TextArea remarquesArea;
    @FXML private ProgressBar progressBar;

    // ----- Champs du formulaire : Propriétaire -----
    @FXML private TextField nomProprietaireField;
    @FXML private TextField prenomProprietaireField;
    @FXML private TextField telephoneProprietaireField;
    @FXML private RadioButton particulierRadio;
    @FXML private RadioButton professionnelRadio;

    // ----- Recherche / filtrage -----
    @FXML private TextField rechercheField;
    @FXML private ComboBox<String> filtreTypeComboBox;

    // ----- Liste et tableau -----
    @FXML private ListView<String> vehiculesListView;
    @FXML private TableView<Vehicule> vehiculeTable;
    @FXML private TableColumn<Vehicule, Integer> colId;
    @FXML private TableColumn<Vehicule, String> colImmat;
    @FXML private TableColumn<Vehicule, String> colMarque;
    @FXML private TableColumn<Vehicule, String> colType;
    @FXML private TableColumn<Vehicule, String> colProprietaire;
    @FXML private TableColumn<Vehicule, String> colTelephone;
    @FXML private TableColumn<Vehicule, String> colActif;
    @FXML private TableColumn<Vehicule, String> colDate;

    private final VehiculeDAO vehiculeDAO = new VehiculeDAO();
    private Vehicule vehiculeSelectionne;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Remplir les ComboBox
        typeComboBox.setItems(FXCollections.observableArrayList("Berline", "SUV", "Camion", "Moto"));
        filtreTypeComboBox.setItems(FXCollections.observableArrayList("Tous", "Berline", "SUV", "Camion", "Moto"));
        filtreTypeComboBox.setValue("Tous");

        // Spinner (valeur entière, min 0, max 999)
        nombreLavagesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 999, 0));

        // Slider remise -> mise à jour du label
        remiseSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                remiseValueLabel.setText(newVal.intValue() + "%"));

        // Colonnes du TableView
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colImmat.setCellValueFactory(new PropertyValueFactory<>("immatriculation"));
        colMarque.setCellValueFactory(new PropertyValueFactory<>("marque"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeVehicule"));
        colProprietaire.setCellValueFactory(data -> {
            Proprietaire p = data.getValue().getProprietaire();
            return new javafx.beans.property.SimpleStringProperty(p != null ? p.toString() : "—");
        });
        colTelephone.setCellValueFactory(data -> {
            Proprietaire p = data.getValue().getProprietaire();
            return new javafx.beans.property.SimpleStringProperty(
                    p != null && p.getTelephone() != null ? p.getTelephone() : "—");
        });
        colActif.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().isActif() ? "Oui" : "Non"));
        colDate.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getDateEnregistrement().toString()));

        // Sélection d'une ligne -> remplir le formulaire
        vehiculeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                remplirFormulaire(newVal);
                vehiculeSelectionne = newVal;
            }
        });

        // Recherche en direct
        rechercheField.textProperty().addListener((obs, oldVal, newVal) -> rechercher());
        filtreTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> rechercher());

        chargerDonnees();
    }

    /**
     * Charge tous les véhicules depuis la base et remplit le tableau + la liste.
     */
    private void chargerDonnees() {
        progressBar.setVisible(true);

        var liste = vehiculeDAO.getTous();
        ObservableList<Vehicule> data = FXCollections.observableArrayList(liste);
        vehiculeTable.setItems(data);

        ObservableList<String> immats = FXCollections.observableArrayList();
        for (Vehicule v : liste) {
            immats.add(v.getImmatriculation() + "  —  " + v.getMarque());
        }
        vehiculesListView.setItems(immats);

        progressBar.setVisible(false);
    }

    /**
     * Recherche / filtre les véhicules selon le texte tapé et le type sélectionné.
     */
    private void rechercher() {
        String motCle = rechercheField.getText() == null ? "" : rechercheField.getText();
        String type = filtreTypeComboBox.getValue();
        var resultats = vehiculeDAO.rechercher(motCle, type);
        vehiculeTable.setItems(FXCollections.observableArrayList(resultats));
    }

    @FXML
    private void handleAjouter(ActionEvent event) {
        if (!validerFormulaire()) return;

        Vehicule v = construireVehiculeDepuisFormulaire();
        boolean succes = vehiculeDAO.ajouter(v);

        if (succes) {
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès",
                    "Véhicule ajouté avec succès !\n"
                    + (v.getProprietaire() != null
                        ? "Propriétaire enregistré : " + v.getProprietaire() + " (id=" + v.getProprietaire().getId() + ")"
                        : "Aucun propriétaire."));
            viderFormulaire();
            chargerDonnees();
        } else {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ajouter le véhicule (ou son propriétaire).\n"
                    + "Vérifiez la console pour le détail de l'erreur SQL.");
        }
    }

    @FXML
    private void handleModifier(ActionEvent event) {
        if (vehiculeSelectionne == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un véhicule à modifier.");
            return;
        }
        if (!validerFormulaire()) return;

        Vehicule v = construireVehiculeDepuisFormulaire();
        v.setId(vehiculeSelectionne.getId());
        if (vehiculeSelectionne.getProprietaire() != null && v.getProprietaire() != null) {
            v.getProprietaire().setId(vehiculeSelectionne.getProprietaire().getId());
        }

        boolean succes = vehiculeDAO.modifier(v);
        if (succes) {
            afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Véhicule modifié avec succès !");
            viderFormulaire();
            chargerDonnees();
        } else {
            afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible de modifier le véhicule.");
        }
    }

    @FXML
    private void handleSupprimer(ActionEvent event) {
        if (vehiculeSelectionne == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un véhicule à supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Voulez-vous vraiment supprimer le véhicule "
                + vehiculeSelectionne.getImmatriculation() + " ?\n"
                + "Tous les lavages liés seront également supprimés.");

        confirmation.showAndWait().ifPresent(reponse -> {
            if (reponse == ButtonType.OK) {
                boolean succes = vehiculeDAO.supprimer(vehiculeSelectionne.getId());
                if (succes) {
                    afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Véhicule supprimé.");
                    viderFormulaire();
                    chargerDonnees();
                } else {
                    afficherAlerte(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le véhicule.");
                }
            }
        });
    }

    @FXML
    private void handleActualiser(ActionEvent event) {
        rechercheField.clear();
        filtreTypeComboBox.setValue("Tous");
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
        if (immatriculationField.getText().isEmpty() || marqueField.getText().isEmpty()
                || typeComboBox.getValue() == null || dateEnregistrementPicker.getValue() == null) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs manquants",
                    "Veuillez remplir au minimum : Immatriculation, Marque, Type et Date.");
            return false;
        }
        if (nomProprietaireField.getText().isEmpty() || prenomProprietaireField.getText().isEmpty()) {
            afficherAlerte(Alert.AlertType.WARNING, "Champs manquants",
                    "Veuillez renseigner le Nom et le Prénom du propriétaire.");
            return false;
        }
        return true;
    }

    private Vehicule construireVehiculeDepuisFormulaire() {
        Vehicule v = new Vehicule();
        v.setImmatriculation(immatriculationField.getText());
        v.setMarque(marqueField.getText());
        v.setModele(modeleField.getText());
        v.setCouleur(couleurField.getText());
        v.setTypeVehicule(typeComboBox.getValue());

        Proprietaire p = new Proprietaire();
        p.setNom(nomProprietaireField.getText());
        p.setPrenom(prenomProprietaireField.getText());
        p.setTelephone(telephoneProprietaireField.getText());
        p.setTypeProprietaire(particulierRadio.isSelected() ? "Particulier" : "Professionnel");
        v.setProprietaire(p);

        v.setActif(actifCheckBox.isSelected());
        v.setDateEnregistrement(dateEnregistrementPicker.getValue());
        v.setNombreLavages(nombreLavagesSpinner.getValue());
        v.setRemise(remiseSlider.getValue());
        v.setRemarques(remarquesArea.getText());
        return v;
    }

    private void remplirFormulaire(Vehicule v) {
        immatriculationField.setText(v.getImmatriculation());
        marqueField.setText(v.getMarque());
        modeleField.setText(v.getModele());
        couleurField.setText(v.getCouleur());
        typeComboBox.setValue(v.getTypeVehicule());
        dateEnregistrementPicker.setValue(v.getDateEnregistrement());
        nombreLavagesSpinner.getValueFactory().setValue(v.getNombreLavages());

        Proprietaire p = v.getProprietaire();
        if (p != null) {
            nomProprietaireField.setText(p.getNom());
            prenomProprietaireField.setText(p.getPrenom());
            telephoneProprietaireField.setText(p.getTelephone());
            if ("Particulier".equals(p.getTypeProprietaire())) {
                particulierRadio.setSelected(true);
            } else {
                professionnelRadio.setSelected(true);
            }
        } else {
            nomProprietaireField.clear();
            prenomProprietaireField.clear();
            telephoneProprietaireField.clear();
            particulierRadio.setSelected(true);
        }

        actifCheckBox.setSelected(v.isActif());
        remiseSlider.setValue(v.getRemise());
        remarquesArea.setText(v.getRemarques());
    }

    private void viderFormulaire() {
        immatriculationField.clear();
        marqueField.clear();
        modeleField.clear();
        couleurField.clear();
        typeComboBox.setValue(null);
        dateEnregistrementPicker.setValue(LocalDate.now());
        nombreLavagesSpinner.getValueFactory().setValue(0);
        nomProprietaireField.clear();
        prenomProprietaireField.clear();
        telephoneProprietaireField.clear();
        particulierRadio.setSelected(true);
        actifCheckBox.setSelected(true);
        remiseSlider.setValue(0);
        remarquesArea.clear();
        vehiculeSelectionne = null;
        vehiculeTable.getSelectionModel().clearSelection();
    }

    private void afficherAlerte(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
