package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe principale de démarrage de l'application JavaFX WashFlow.
 * Gère également la navigation entre les différentes scènes (FXML).
 */
public class MainApp extends Application {

    private static Stage stagePrincipal;

    @Override
    public void start(Stage stage) throws Exception {
        stagePrincipal = stage;
        stagePrincipal.setTitle("WashFlow - Gestion de Station de Lavage");

        changerScene("vue/Login.fxml");

        stagePrincipal.show();
    }

    /**
     * Change la scène affichée dans la fenêtre principale.
     * @param cheminFxml chemin relatif du fichier FXML (depuis resources/)
     */
    public static void changerScene(String cheminFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getClassLoader().getResource(cheminFxml));
            Parent racine = loader.load();
            Scene scene = new Scene(racine);
            scene.getStylesheets().add(MainApp.class.getClassLoader()
                    .getResource("vue/Style.css").toExternalForm());

            stagePrincipal.setScene(scene);
            stagePrincipal.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
