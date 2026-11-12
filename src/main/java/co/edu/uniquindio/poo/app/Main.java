package co.edu.uniquindio.poo.app;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class Main extends Application {
    @Override public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/poo/fxml/principal.fxml"));
        Scene scene = new Scene(loader.load(), 1120, 840);
        stage.setTitle("Monedero Virtual - Proyecto Final de Programación I");
        stage.setScene(scene);
        stage.setMinWidth(1050);
        stage.setMinHeight(780);
        stage.show();
    }
    public static void main(String[] args) { launch(args); }
}
