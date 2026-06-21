package utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Classe utilitaire affichant la boîte de dialogue "À propos" de l'application.
 * Les couleurs sont fixées en style inline pour garantir un bon contraste,
 * indépendamment du thème CSS par défaut des boîtes de dialogue JavaFX.
 */
public class AProposDialog {

    private static final String FOND = "#0f1626";
    private static final String TEXTE_PRINCIPAL = "#ffffff";
    private static final String TEXTE_SECONDAIRE = "#aab4d4";
    private static final String ACCENT = "#5b7cfa";

    public static void afficher() {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("À propos de WashFlow");

        VBox contenu = new VBox(12);
        contenu.setAlignment(Pos.CENTER);
        contenu.setPadding(new Insets(24));
        contenu.setStyle("-fx-background-color: " + FOND + ";");

        Label logo = new Label("💧");
        logo.setStyle("-fx-font-size: 38px;");

        Label nom = new Label("WashFlow");
        nom.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXTE_PRINCIPAL + ";");

        Label version = new Label("Version 1.0.0");
        version.setStyle("-fx-font-size: 12px; -fx-text-fill: " + ACCENT + ";");

        Label description = new Label(
                "Application de gestion d'une station de lavage automobile.\n"
                        + "Gérez vos véhicules, vos lavages et suivez vos statistiques en temps réel.");
        description.setWrapText(true);
        description.setMaxWidth(380);
        description.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXTE_SECONDAIRE + ";");
        description.setAlignment(Pos.CENTER);
        description.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label copyright = new Label("© 2026 WashFlow. Tous droits réservés.");
        copyright.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXTE_SECONDAIRE + ";");

        Label licencesTitre = new Label("Licences Open Source");
        licencesTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; "
                + "-fx-text-fill: " + TEXTE_PRINCIPAL + "; -fx-padding: 12 0 0 0;");

        Label licences = new Label(
                "• JavaFX — GPL v2 avec Classpath Exception\n"
                        + "• MySQL Connector/J — GPL v2");
        licences.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXTE_SECONDAIRE + ";");
        licences.setAlignment(Pos.CENTER);
        licences.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        contenu.getChildren().addAll(
                logo, nom, version, description, copyright,
                licencesTitre, licences
        );

        alert.getDialogPane().setContent(contenu);
        alert.getDialogPane().setStyle("-fx-background-color: " + FOND + ";");
        alert.getButtonTypes().add(ButtonType.CLOSE);

        // Force aussi le style du bouton "Close" pour rester lisible sur fond sombre
        alert.getDialogPane().lookupButton(ButtonType.CLOSE)
                .setStyle("-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-font-weight: bold;");

        alert.showAndWait();
    }
}
