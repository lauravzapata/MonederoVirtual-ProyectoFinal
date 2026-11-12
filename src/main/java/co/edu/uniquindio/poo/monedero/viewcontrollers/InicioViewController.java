package co.edu.uniquindio.poo.monedero.viewcontrollers;

import co.edu.uniquindio.poo.monedero.app.Aplicacion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import co.edu.uniquindio.poo.monedero.model.Cliente;
import co.edu.uniquindio.poo.monedero.model.Monedero;

public class InicioViewController {
    private Aplicacion aplicacion;
    @FXML private Label lblTotalClientes;
    @FXML private Label lblTotalMonederos;
    @FXML private Label lblSaldoTotal;

    public void setApp(Aplicacion aplicacion) {
        this.aplicacion = aplicacion;
        int monederos = 0;
        double saldo = 0;
        for (Cliente cliente : Aplicacion.monederoVirtual.getListaClientes()) {
            monederos += cliente.getMonederos().size();
            for (Monedero monedero : cliente.getMonederos()) saldo += monedero.getSaldo();
        }
        lblTotalClientes.setText(String.valueOf(Aplicacion.monederoVirtual.getListaClientes().size()));
        lblTotalMonederos.setText(String.valueOf(monederos));
        lblSaldoTotal.setText("$" + String.format("%.2f", saldo));
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
