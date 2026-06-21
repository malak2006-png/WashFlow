package controleur;

import application.MainApp;
import dao.UtilisateurDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import modele.Utilisateur;

/**
 * Contrôleur de la page de connexion (Login.fxml).
 * Vérifie les identifiants via la table MySQL "utilisateur".
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        Utilisateur utilisateur = utilisateurDAO.authentifier(username, password);

        if (utilisateur != null) {
            errorLabel.setText("");
            // Connexion réussie -> ouvrir le Dashboard
            try {
                MainApp.changerScene("vue/Dashboard.fxml");
            } catch (Exception e) {
                e.printStackTrace();
                errorLabel.setText("Erreur lors du chargement de l'application.");
            }
        } else {
            errorLabel.setText("Nom d'utilisateur ou mot de passe incorrect.");
        }
    }
}
