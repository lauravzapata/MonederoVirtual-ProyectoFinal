package co.edu.uniquindio.poo.monedero.viewcontrollers;

import co.edu.uniquindio.poo.monedero.app.Aplicacion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class InicioViewController {
    private Aplicacion aplicacion;

    public void setApp(Aplicacion aplicacion) {
        this.aplicacion = aplicacion;
    }

    @FXML
    void abrirClientesEvent(ActionEvent event) {
        aplicacion.openViewClientes();
    }

    @FXML
    void abrirMonederoEvent(ActionEvent event) {
        aplicacion.openViewMonedero();
    }
}
