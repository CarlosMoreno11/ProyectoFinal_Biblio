package _Bibliotecapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

public class Main extends Application {

    static {
        System.setProperty("prism.order", "sw");
        System.setProperty("glass.accessible.force", "false");
    }

    @Override
    public void init() {
        System.out.println("Intentando conectar a MongoDB...");
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("bibliotecapp");
            long numLibros = database.getCollection("libros").countDocuments();
            System.out.println("Conexión exitosa a MongoDB.");
            System.out.println("Libros actuales: " + numLibros);
        } catch (Exception e) {
            System.err.println("Error MongoDB: " + e.getMessage());
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/ventana_principal.fxml"));
                Parent root = loader.load();

                Scene scene = new Scene(root);
                primaryStage.setTitle("Bibliotecapp - Panel de Control");                primaryStage.setScene(scene);
                
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
