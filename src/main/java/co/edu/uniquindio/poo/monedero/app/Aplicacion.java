package co.edu.uniquindio.poo.monedero.app;

import co.edu.uniquindio.poo.monedero.model.MonederoVirtual;
import co.edu.uniquindio.poo.monedero.viewcontrollers.InicioViewController;
import co.edu.uniquindio.poo.monedero.viewcontrollers.ClienteViewController;
import co.edu.uniquindio.poo.monedero.viewcontrollers.MonederoViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;

public class Aplicacion extends Application {
    private Stage primaryStage;
    public static final MonederoVirtual monederoVirtual = new MonederoVirtual("Monedero Virtual");

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle(monederoVirtual.getNombre());
        primaryStage.setMinWidth(1050);
        primaryStage.setMinHeight(780);
        openViewInicio();
    }

    public void openViewInicio() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Aplicacion.class.getResource("/co/edu/uniquindio/poo/monedero/inicio.fxml"));
            AnchorPane rootLayout = loader.load();
            InicioViewController viewController = loader.getController();
            viewController.setApp(this);
            Scene scene = new Scene(rootLayout, 1120, 840);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo abrir la pantalla Inicio.", e);
        }
    }

    public void openViewClientes() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Aplicacion.class.getResource("/co/edu/uniquindio/poo/monedero/clientes.fxml"));
            AnchorPane rootLayout = loader.load();
            ClienteViewController viewController = loader.getController();
            viewController.setApp(this);
            Scene scene = new Scene(rootLayout, 1120, 840);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo abrir la pantalla Clientes.", e);
        }
    }

    public void openViewMonedero() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Aplicacion.class.getResource("/co/edu/uniquindio/poo/monedero/monedero.fxml"));
            AnchorPane rootLayout = loader.load();
            MonederoViewController viewController = loader.getController();
            viewController.setApp(this);
            Scene scene = new Scene(rootLayout, 1120, 840);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo abrir la pantalla Monedero.", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
