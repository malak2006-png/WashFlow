package controleur;

import application.MainApp;
import dao.LavageDAO;
import dao.VehiculeDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import modele.Lavage;
import modele.Vehicule;
import utils.CsvExporter;

import java.io.File;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contrôleur du Tableau de Bord (Dashboard.fxml).
 * Navigation par sections (menu latéral) : chaque section est construite
 * dynamiquement en Java et injectée dans contenuSectionBox.
 */
public class TableauDeBordController implements Initializable {

    @FXML private Label dateActualisationLabel;
    @FXML private VBox contenuSectionBox;

    @FXML private ToggleButton btnSectionKpis;
    @FXML private ToggleButton btnSectionOperationnel;
    @FXML private ToggleButton btnSectionRepartition;
    @FXML private ToggleButton btnSectionEvolution;
    @FXML private ToggleButton btnSectionExport;

    private final VehiculeDAO vehiculeDAO = new VehiculeDAO();
    private final LavageDAO lavageDAO = new LavageDAO();

    private File dossierExport;
    private Label exportStatusLabel; // créé dynamiquement dans la section Export

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dateActualisationLabel.setText("Actualisé le "
                + java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                + "  ·  Station Principale");

        afficherSectionKpis();
    }

    // ===================================================================
    // Navigation entre sections
    // ===================================================================

    @FXML private void handleAfficherKpis(ActionEvent e) { afficherSectionKpis(); }
    @FXML private void handleAfficherOperationnel(ActionEvent e) { afficherSectionOperationnel(); }
    @FXML private void handleAfficherRepartition(ActionEvent e) { afficherSectionRepartition(); }
    @FXML private void handleAfficherEvolution(ActionEvent e) { afficherSectionEvolution(); }
    @FXML private void handleAfficherExport(ActionEvent e) { afficherSectionExport(); }

    /**
     * Construit l'en-tête commun à chaque section : barre colorée + titre + description.
     */
    private VBox creerEnTeteSection(String couleurHex, String titre, String description) {
        Region barre = new Region();
        barre.getStyleClass().add("dash-section-titre-bar");
        barre.setStyle("-fx-background-color: " + couleurHex + ";");

        Label titreLabel = new Label(titre);
        titreLabel.getStyleClass().add("dash-section-titre");

        HBox ligneTitre = new HBox(10, barre, titreLabel);
        ligneTitre.setAlignment(Pos.CENTER_LEFT);

        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("dash-section-desc");

        VBox enTete = new VBox(6, ligneTitre, descLabel);
        return enTete;
    }

    private VBox creerCarteKpi(String label, String valeur) {
        Label labelTitre = new Label(label);
        labelTitre.getStyleClass().add("dash-carte-label");
        Label labelValeur = new Label(valeur);
        labelValeur.getStyleClass().add("dash-carte-valeur");

        VBox carte = new VBox(8, labelTitre, labelValeur);
        carte.getStyleClass().add("dash-carte");
        HBox.setHgrow(carte, Priority.ALWAYS);
        carte.setMaxWidth(Double.MAX_VALUE);
        return carte;
    }

    // ===================================================================
    // Section 1 : KPIs Financiers
    // ===================================================================
    private void afficherSectionKpis() {
        List<Lavage> lavages = lavageDAO.getTous();
        double revenuTotal = lavageDAO.getRevenuTotal();
        int lavagesAujourdhui = lavageDAO.getLavagesAujourdhui();
        int totalLavages = lavageDAO.getTotalLavages();

        double caHebdo = revenuTotal / 4.0;   // estimation simple répartie sur le mois
        double caJournalier = lavages.stream()
                .filter(l -> l.getDateLavage().isEqual(java.time.LocalDate.now()))
                .filter(Lavage::isPaye)
                .mapToDouble(Lavage::getPrix).sum();
        double panierMoyen = totalLavages > 0 ? revenuTotal / totalLavages : 0;

        VBox enTete = creerEnTeteSection("#3b5bdb", "KPIs Financiers",
                "CA journalier, hebdo, mensuel et panier moyen avec répartition par service");

        HBox cartes = new HBox(16,
                creerCarteKpi("CA JOURNALIER", String.format("%.0f DH", caJournalier)),
                creerCarteKpi("CA HEBDOMADAIRE", String.format("%.0f DH", caHebdo)),
                creerCarteKpi("CA MENSUEL", String.format("%.0f DH", revenuTotal)),
                creerCarteKpi("PANIER MOYEN", String.format("%.0f DH", panierMoyen))
        );

        contenuSectionBox.getChildren().setAll(enTete, cartes);
    }

    // ===================================================================
    // Section 2 : Indicateurs Opérationnels
    // ===================================================================
    private void afficherSectionOperationnel() {
        int lavagesAujourdhui = lavageDAO.getLavagesAujourdhui();
        List<Lavage> lavages = lavageDAO.getTous();

        double dureeMoyenne = lavages.stream().mapToInt(Lavage::getDuree).average().orElse(0);
        long enCours = lavages.stream().filter(l -> l.getEtat().equals("En cours")).count();

        int totalBaies = 4;
        int baiesOccupees = (int) Math.min(enCours, totalBaies);
        double tauxOccupation = totalBaies > 0 ? (double) baiesOccupees / totalBaies * 100 : 0;

        VBox enTete = creerEnTeteSection("#2dd4bf", "Indicateurs Opérationnels",
                "Véhicules lavés, temps moyen et taux d'occupation des baies");

        HBox cartes = new HBox(16,
                creerCarteKpi("VÉHICULES LAVÉS (JOUR)", String.valueOf(lavagesAujourdhui)),
                creerCarteKpi("TEMPS MOYEN / VÉHICULE", String.format("%.0f min", dureeMoyenne)),
                creerCarteKpi("TAUX D'OCCUPATION DES BAIES", String.format("%.0f%%", tauxOccupation))
        );

        // Carte "Occupation actuelle des baies"
        Label titreOccupation = new Label("Occupation actuelle des baies de lavage");
        titreOccupation.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        Label compteurBaies = new Label(baiesOccupees + " / " + totalBaies + " baies");
        compteurBaies.setStyle("-fx-text-fill: #2dd4bf; -fx-font-weight: bold;");

        HBox ligneTitreBaies = new HBox(titreOccupation);
        HBox.setHgrow(titreOccupation, Priority.ALWAYS);
        ligneTitreBaies.getChildren().add(compteurBaies);

        ProgressBar barreOccupation = new ProgressBar(totalBaies > 0 ? (double) baiesOccupees / totalBaies : 0);
        barreOccupation.setMaxWidth(Double.MAX_VALUE);
        barreOccupation.setStyle("-fx-accent: #2dd4bf;");

        HBox baiesBox = new HBox(12);
        for (int i = 1; i <= totalBaies; i++) {
            boolean occupee = i <= baiesOccupees;
            String couleurTexte = occupee ? "#2dd4bf" : "#7a84a8";

            Label iconeLabel = new Label("🚙");
            Label numeroLabel = new Label("Baie " + i);
            numeroLabel.setStyle("-fx-text-fill: " + couleurTexte + ";");
            Label etatLabel = new Label(occupee ? "Occupée" : "Libre");
            etatLabel.setStyle("-fx-text-fill: " + couleurTexte + "; -fx-font-weight: bold;");

            VBox baie = new VBox(4, iconeLabel, numeroLabel, etatLabel);
            baie.setAlignment(Pos.CENTER_LEFT);
            baie.setPadding(new Insets(12));
            baie.setStyle(occupee
                    ? "-fx-background-color: #123a36; -fx-background-radius: 10; -fx-border-color: #2dd4bf; -fx-border-radius: 10;"
                    : "-fx-background-color: #111729; -fx-background-radius: 10; -fx-border-color: #1d2746; -fx-border-radius: 10;");
            HBox.setHgrow(baie, Priority.ALWAYS);
            baie.setMaxWidth(Double.MAX_VALUE);
            baiesBox.getChildren().add(baie);
        }

        VBox carteBaies = new VBox(14, ligneTitreBaies, barreOccupation, baiesBox);
        carteBaies.getStyleClass().add("dash-carte");

        contenuSectionBox.getChildren().setAll(enTete, cartes, carteBaies);
    }

    // ===================================================================
    // Section 3 : Répartition des Lavages (Donut chart)
    // ===================================================================
    private void afficherSectionRepartition() {
        VBox enTete = creerEnTeteSection("#fbbf24", "Répartition des Lavages",
                "Part de chaque formule (Premium, Complet, Basic) sur le mois en cours");

        List<Object[]> repartition = lavageDAO.getRepartitionParType();
        int total = repartition.stream().mapToInt(o -> (Integer) o[1]).sum();

        PieChart pieChart = new PieChart();
        pieChart.setLegendVisible(false);
        pieChart.setLabelsVisible(false);
        pieChart.setStartAngle(90);
        pieChart.setPrefHeight(320);

        String[] couleurs = {"#fb7185", "#fbbf24", "#2dd4bf", "#818cf8"};
        VBox legende = new VBox(14);
        int index = 0;

        for (Object[] ligne : repartition) {
            String type = (String) ligne[0];
            int nombre = (Integer) ligne[1];
            double pourcentage = total > 0 ? (nombre * 100.0 / total) : 0;

            PieChart.Data data = new PieChart.Data(type, nombre);
            pieChart.getData().add(data);

            String couleur = couleurs[index % couleurs.length];

            Region pastille = new Region();
            pastille.setMinSize(10, 10);
            pastille.setMaxSize(10, 10);
            pastille.setStyle("-fx-background-color: " + couleur + "; -fx-background-radius: 5;");

            Label nomLabel = new Label(type);
            nomLabel.getStyleClass().add("dash-legende-nom");
            HBox.setHgrow(nomLabel, Priority.ALWAYS);

            Label pourcentageLabel = new Label(String.format("%.0f%%", pourcentage));
            pourcentageLabel.getStyleClass().add("dash-legende-valeur");

            HBox ligneNom = new HBox(8, pastille, nomLabel, pourcentageLabel);
            ligneNom.setAlignment(Pos.CENTER_LEFT);

            ProgressBar miniBarre = new ProgressBar(pourcentage / 100.0);
            miniBarre.setMaxWidth(Double.MAX_VALUE);
            miniBarre.setStyle("-fx-accent: " + couleur + ";");

            legende.getChildren().addAll(ligneNom, miniBarre);
            index++;
        }

        // Applique les couleurs au PieChart après affichage
        pieChart.applyCss();
        pieChart.layout();
        for (int i = 0; i < pieChart.getData().size(); i++) {
            PieChart.Data d = pieChart.getData().get(i);
            String c = couleurs[i % couleurs.length];
            if (d.getNode() != null) {
                d.getNode().setStyle("-fx-pie-color: " + c + ";");
            }
        }

        HBox graphiqueEtLegende = new HBox(30, pieChart, legende);
        graphiqueEtLegende.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(pieChart, Priority.ALWAYS);
        HBox.setHgrow(legende, Priority.ALWAYS);

        VBox carte = new VBox(16,
                new Label("RÉPARTITION PAR FORMULE — MOIS EN COURS") {{ getStyleClass().add("dash-carte-label"); }},
                graphiqueEtLegende);
        carte.getStyleClass().add("dash-carte");

        contenuSectionBox.getChildren().setAll(enTete, carte);
    }

    // ===================================================================
    // Section 4 : Évolution Mensuelle (Bar chart)
    // ===================================================================
    private void afficherSectionEvolution() {
        VBox enTete = creerEnTeteSection("#818cf8", "Évolution Mensuelle",
                "Nombre de lavages réalisés chaque mois sur les 12 derniers mois");

        List<Lavage> lavages = lavageDAO.getTous();
        String[] mois = {"Jan", "Fév", "Mar", "Avr", "Mai", "Jun", "Jul", "Aoû", "Sep", "Oct", "Nov", "Déc"};
        int[] compteurs = new int[12];

        for (Lavage l : lavages) {
            int moisIndex = l.getDateLavage().getMonthValue() - 1;
            compteurs[moisIndex]++;
        }

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Mois");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Nombre");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);
        barChart.setPrefHeight(340);
        barChart.setStyle("-fx-bar-fill: #818cf8;");

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        for (int i = 0; i < 12; i++) {
            serie.getData().add(new XYChart.Data<>(mois[i], compteurs[i]));
        }
        barChart.getData().add(serie);

        VBox carte = new VBox(16,
                new Label("NOMBRE DE LAVAGES — 12 DERNIERS MOIS") {{ getStyleClass().add("dash-carte-label"); }},
                barChart);
        carte.getStyleClass().add("dash-carte");

        contenuSectionBox.getChildren().setAll(enTete, carte);

        // Applique la couleur violette aux barres une fois rendues
        barChart.applyCss();
        barChart.layout();
        serie.getData().forEach(d -> {
            if (d.getNode() != null) {
                d.getNode().setStyle("-fx-bar-fill: #818cf8;");
            }
        });
    }

    // ===================================================================
    // Section 5 : Export & Personnalisation
    // ===================================================================
    private void afficherSectionExport() {
        VBox enTete = creerEnTeteSection("#fb923c", "Export & Personnalisation",
                "Export CSV des données et personnalisation de la couleur d'accent de l'application");

        // ----- Carte Export -----
        TextField dossierExportField = new TextField("Aucun dossier sélectionné");
        dossierExportField.setEditable(false);
        dossierExportField.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(dossierExportField, Priority.ALWAYS);

        Button choisirDossierBtn = new Button("📁 Choisir dossier");
        choisirDossierBtn.getStyleClass().add("btn-actualiser");
        choisirDossierBtn.setOnAction(e -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Choisir le dossier d'export");
            File dossier = chooser.showDialog(choisirDossierBtn.getScene().getWindow());
            if (dossier != null) {
                dossierExport = dossier;
                dossierExportField.setText(dossier.getAbsolutePath());
            }
        });

        HBox ligneDossier = new HBox(10, dossierExportField, choisirDossierBtn);
        ligneDossier.setAlignment(Pos.CENTER_LEFT);

        Button exporterVehiculesBtn = new Button("⬇ Exporter CSV Véhicules");
        exporterVehiculesBtn.getStyleClass().add("btn-ajouter");
        exporterVehiculesBtn.setMaxWidth(Double.MAX_VALUE);
        exporterVehiculesBtn.setOnAction(this::handleExporterVehicules);

        Button exporterLavagesBtn = new Button("⬇ Exporter CSV Lavages");
        exporterLavagesBtn.getStyleClass().add("btn-ajouter");
        exporterLavagesBtn.setMaxWidth(Double.MAX_VALUE);
        exporterLavagesBtn.setOnAction(this::handleExporterLavages);

        exportStatusLabel = new Label("En attente d'export...");
        exportStatusLabel.getStyleClass().add("sous-titre");

        Label infoCsv = new Label("Les fichiers CSV sont encodés en UTF-8 et compatibles avec Excel, LibreOffice et Google Sheets.");
        infoCsv.setWrapText(true);
        infoCsv.getStyleClass().add("sous-titre");

        VBox carteExport = new VBox(14,
                new Label("⬇ Exporter les Données") {{ setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;"); }},
                new Label("Dossier de destination") {{ getStyleClass().add("label-champ"); }},
                ligneDossier, exporterVehiculesBtn, exporterLavagesBtn, exportStatusLabel, infoCsv);
        carteExport.getStyleClass().add("dash-carte");

        // ----- Carte Personnalisation -----
        ColorPicker couleurAccentPicker = new ColorPicker(Color.web("#3b5bdb"));
        couleurAccentPicker.getStyleClass().add("color-picker");
        Tooltip tooltipColor = new Tooltip("Choisissez la couleur d'accent de l'application");
        Tooltip.install(couleurAccentPicker, tooltipColor);
        couleurAccentPicker.setOnAction(e -> {
            Color couleur = couleurAccentPicker.getValue();
            String hex = String.format("#%02X%02X%02X",
                    (int) (couleur.getRed() * 255),
                    (int) (couleur.getGreen() * 255),
                    (int) (couleur.getBlue() * 255));
            couleurAccentPicker.getScene().getRoot().setStyle("-fx-accent: " + hex + ";");
            exportStatusLabel.setText("Couleur d'accent mise à jour : " + hex);
        });

        Label labelCouleur = new Label("Couleur d'accent :");
        labelCouleur.getStyleClass().add("label-champ");

        HBox ligneCouleur = new HBox(14, labelCouleur, couleurAccentPicker);
        ligneCouleur.setAlignment(Pos.CENTER_LEFT);

        VBox cartePersonnalisation = new VBox(12,
                new Label("🎨 Personnalisation de l'interface") {{ setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;"); }},
                ligneCouleur);
        cartePersonnalisation.getStyleClass().add("dash-carte");

        contenuSectionBox.getChildren().setAll(enTete, carteExport, cartePersonnalisation);
    }

    // ===================================================================
    // Export CSV
    // ===================================================================

    @FXML
    private void handleExporterVehicules(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les véhicules en CSV");
        fileChooser.setInitialFileName("vehicules.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
        if (dossierExport != null) {
            fileChooser.setInitialDirectory(dossierExport);
        }

        javafx.stage.Window fenetre = contenuSectionBox.getScene() != null
                ? contenuSectionBox.getScene().getWindow() : null;
        File fichier = fileChooser.showSaveDialog(fenetre);
        if (fichier != null) {
            List<Vehicule> vehicules = vehiculeDAO.getTous();
            boolean succes = CsvExporter.exporterVehicules(vehicules, fichier.getAbsolutePath());
            if (exportStatusLabel != null) {
                exportStatusLabel.setText(succes ? "Fichier exporté avec succès ✓" : "Échec de l'export.");
            }
        }
    }

    private void handleExporterLavages(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les lavages en CSV");
        fileChooser.setInitialFileName("lavages.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
        if (dossierExport != null) {
            fileChooser.setInitialDirectory(dossierExport);
        }

        javafx.stage.Window fenetre = contenuSectionBox.getScene() != null
                ? contenuSectionBox.getScene().getWindow() : null;
        File fichier = fileChooser.showSaveDialog(fenetre);
        if (fichier != null) {
            List<Lavage> lavages = lavageDAO.getTous();
            boolean succes = CsvExporter.exporterLavages(lavages, fichier.getAbsolutePath());
            if (exportStatusLabel != null) {
                exportStatusLabel.setText(succes ? "Fichier exporté avec succès ✓" : "Échec de l'export.");
            }
        }
    }

    // ----- Navigation (MenuBar) -----
    @FXML private void handleAllerVehicules(ActionEvent event) { MainApp.changerScene("vue/GestionVehicule.fxml"); }
    @FXML private void handleAllerLavages(ActionEvent event) { MainApp.changerScene("vue/GestionLavage.fxml"); }
    @FXML private void handleAllerDashboard(ActionEvent event) { MainApp.changerScene("vue/Dashboard.fxml"); }
    @FXML private void handleQuitter(ActionEvent event) { System.exit(0); }
    @FXML private void handleAPropos(ActionEvent event) { utils.AProposDialog.afficher(); }
}
