package co.edu.uniquindio.poo.monedero.viewcontrollers;

import co.edu.uniquindio.poo.monedero.app.Aplicacion;
import co.edu.uniquindio.poo.monedero.controller.ClienteController;
import co.edu.uniquindio.poo.monedero.model.Cliente;
import co.edu.uniquindio.poo.monedero.exceptions.CuentaNoEncontradaException;
import co.edu.uniquindio.poo.monedero.exceptions.DatoInvalidoException;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ClienteViewController {
    private Aplicacion aplicacion;
    private ClienteController clienteController;

    @FXML private TextField txtDocumento;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private Label lblClientes;
    @FXML private Label lblResultado;

    public void setApp(Aplicacion aplicacion) {
        this.aplicacion = aplicacion;
        this.clienteController = new ClienteController(Aplicacion.monederoVirtual);
        mostrarClientes();
    }

    @FXML
    void registrarClienteEvent(ActionEvent event) {
        try {
            Cliente nuevo = clienteController.registrarCliente(txtDocumento.getText(), txtNombre.getText());
            mostrarClientes();
            cmbCliente.setValue(nuevo);
            limpiarCampos();
            lblResultado.setText("Cliente registrado: " + nuevo);
        } catch (DatoInvalidoException e) {
            lblResultado.setText(e.getMessage());
        }
    }

    @FXML
    void editarClienteEvent(ActionEvent event) {
        try {
            clienteController.editarNombre(cmbCliente.getValue(), txtNombre.getText());
            mostrarClientes();
            limpiarCampos();
            lblResultado.setText("Nombre actualizado correctamente.");
        } catch (DatoInvalidoException e) {
            lblResultado.setText(e.getMessage());
        }
    }

    @FXML
    void eliminarClienteEvent(ActionEvent event) {
        Cliente cliente = cmbCliente.getValue();
        if (cliente == null) {
            lblResultado.setText("Selecciona un cliente.");
            return;
        }
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar al cliente " + cliente.getNombre() + "?", ButtonType.OK, ButtonType.CANCEL);
        alerta.setHeaderText("Eliminar cliente");
        if (alerta.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try {
            clienteController.eliminarCliente(cliente.getDocumento());
            cmbCliente.setValue(null);
            mostrarClientes();
            lblResultado.setText("Cliente eliminado.");
        } catch (DatoInvalidoException | CuentaNoEncontradaException e) {
            lblResultado.setText(e.getMessage());
        }
    }

    @FXML
    void volverEvent(ActionEvent event) {
        aplicacion.openViewInicio();
    }

    private void mostrarClientes() {
        Cliente seleccionado = cmbCliente.getValue();
        cmbCliente.setItems(FXCollections.observableArrayList(clienteController.obtenerClientes()));
        cmbCliente.setValue(seleccionado);
        String texto = "";
        for (Cliente cliente : clienteController.obtenerClientes()) {
            texto = texto + cliente + "\n";
        }
        lblClientes.setText(texto.isEmpty() ? "Todavía no hay clientes registrados." : texto);
    }

    private void limpiarCampos() {
        txtDocumento.setText("");
        txtNombre.setText("");
    }
}
