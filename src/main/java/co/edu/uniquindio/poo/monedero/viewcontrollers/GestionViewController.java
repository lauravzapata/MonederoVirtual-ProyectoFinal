package co.edu.uniquindio.poo.monedero.viewcontrollers;

import co.edu.uniquindio.poo.monedero.app.Aplicacion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

public class GestionViewController {
    private Aplicacion aplicacion;
    private ClienteViewController clienteViewController;
    private MonederoViewController monederoViewController;

    @FXML private TabPane tabPane;
    @FXML private Tab tabClientes;
    @FXML private Tab tabMonedero;

    @FXML
    void initialize() throws IOException {
        FXMLLoader loaderClientes = new FXMLLoader(getClass().getResource(
                "/co/edu/uniquindio/poo/monedero/clientes.fxml"));
        AnchorPane vistaClientes = loaderClientes.load();
        clienteViewController = loaderClientes.getController();
        tabClientes.setContent(vistaClientes);

        FXMLLoader loaderMonedero = new FXMLLoader(getClass().getResource(
                "/co/edu/uniquindio/poo/monedero/monedero.fxml"));
        AnchorPane vistaMonedero = loaderMonedero.load();
        monederoViewController = loaderMonedero.getController();
        tabMonedero.setContent(vistaMonedero);

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, anterior, actual) -> {
            if (actual == tabClientes) clienteViewController.actualizarTabla();
            if (actual == tabMonedero) monederoViewController.actualizarDatos();
        });
    }

    public void setApp(Aplicacion aplicacion) {
        this.aplicacion = aplicacion;
    }

    public void mostrarClientes() {
        tabPane.getSelectionModel().select(tabClientes);
        clienteViewController.actualizarTabla();
    }

    public void mostrarMonedero() {
        tabPane.getSelectionModel().select(tabMonedero);
        monederoViewController.actualizarDatos();
    }

    @FXML
    void volver() {
        aplicacion.openViewInicio();
    }
}
