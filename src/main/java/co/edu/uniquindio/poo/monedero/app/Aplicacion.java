package co.edu.uniquindio.poo.monedero.app;

import co.edu.uniquindio.poo.monedero.model.MonederoVirtual;
import co.edu.uniquindio.poo.monedero.viewcontrollers.InicioViewController;
import co.edu.uniquindio.poo.monedero.viewcontrollers.GestionViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;

public class Aplicacion extends Application {
    private Stage primaryStage;
    private GestionViewController gestionViewController;
    public static final MonederoVirtual monederoVirtual = new MonederoVirtual("Monedero virtual");

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle(monederoVirtual.getNombre());
        primaryStage.setMinWidth(1040);
        primaryStage.setMinHeight(780);
        openViewInicio();
    }

    public void openViewInicio() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Aplicacion.class.getResource("/co/edu/uniquindio/poo/monedero/inicio.fxml"));
            AnchorPane rootLayout = loader.load();
            InicioViewController inicioViewController = loader.getController();
            inicioViewController.setApp(this);
            Scene scene = new Scene(rootLayout, 1120, 840);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo abrir Inicio.", e);
        }
    }

    public void openGestion() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Aplicacion.class.getResource("/co/edu/uniquindio/poo/monedero/gestion.fxml"));
            AnchorPane rootLayout = loader.load();
            gestionViewController = loader.getController();
            gestionViewController.setApp(this);
            Scene scene = new Scene(rootLayout, 1120, 840);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo abrir Gestión.", e);
        }
    }

    public void openViewClientes() {
        openGestion();
        gestionViewController.mostrarClientes();
    }

    public void openViewMonedero() {
        openGestion();
        gestionViewController.mostrarMonedero();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
