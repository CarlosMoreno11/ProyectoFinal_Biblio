package _Bibliotecapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    static {
        System.setProperty("prism.order", "sw");
        System.setProperty("glass.accessible.force", "false");
    }

    @Override
    public void start(Stage primaryStage) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/ventana_principal.fxml"));
                Parent root = loader.load();

                Scene scene = new Scene(root);
                primaryStage.setTitle("Bibliotecapp - Panel de Control");
                primaryStage.setScene(scene);
                
                primaryStage.show();
                System.out.println("¡Interfaz cargada correctamente!");

            } catch (Exception e) {
                System.err.println("Error al cargar la interfaz FXML:");
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        System.setProperty("prism.order", "sw");
        launch(args);
    }
}
