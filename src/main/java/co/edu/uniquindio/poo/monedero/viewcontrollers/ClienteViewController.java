package co.edu.uniquindio.poo.monedero.viewcontrollers;

import co.edu.uniquindio.poo.monedero.app.Aplicacion;
import co.edu.uniquindio.poo.monedero.controller.ClienteController;
import co.edu.uniquindio.poo.monedero.model.Cliente;
import co.edu.uniquindio.poo.monedero.exceptions.CuentaNoEncontradaException;
import co.edu.uniquindio.poo.monedero.exceptions.DatoInvalidoException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ClienteViewController {
    private ClienteController clienteController;
    private final ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
    private Cliente selectedCliente;

    @FXML private TextField txtDocumento;
    @FXML private TextField txtNombre;
    @FXML private Label lblResultado;
    @FXML private TableView<Cliente> tblClientes;
    @FXML private TableColumn<Cliente, String> tbcDocumento;
    @FXML private TableColumn<Cliente, String> tbcNombre;
    @FXML private TableColumn<Cliente, String> tbcPuntos;
    @FXML private TableColumn<Cliente, String> tbcRango;

    @FXML
    void initialize() {
        clienteController = new ClienteController(Aplicacion.monederoVirtual);
        initDataBinding();
        tblClientes.setItems(listaClientes);
        actualizarTabla();
        listenerSelection();
    }

    private void initDataBinding() {
        tbcDocumento.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDocumento()));
        tbcNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        tbcPuntos.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getPuntos())));
        tbcRango.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRango()));
    }

    private void listenerSelection() {
        tblClientes.getSelectionModel().selectedItemProperty().addListener((obs, anterior, actual) -> {
            selectedCliente = actual;
            if (actual != null) mostrarCliente(actual);
        });
    }

    private void mostrarCliente(Cliente cliente) {
        txtDocumento.setText(cliente.getDocumento());
        txtNombre.setText(cliente.getNombre());
        txtDocumento.setDisable(true);
    }

    @FXML
    void registrarCliente() {
        try {
            Cliente nuevo = clienteController.registrarCliente(txtDocumento.getText(), txtNombre.getText());
            actualizarTabla();
            limpiarCampos();
            lblResultado.setText("Cliente registrado: " + nuevo);
        } catch (DatoInvalidoException e) {
            lblResultado.setText(e.getMessage());
        }
    }

    @FXML
    void actualizarCliente() {
        if (selectedCliente == null) {
            lblResultado.setText("Seleccione un cliente de la tabla.");
            return;
        }
        try {
            clienteController.editarNombre(selectedCliente, txtNombre.getText());
            tblClientes.refresh();
            limpiarCampos();
            lblResultado.setText("Cliente actualizado.");
        } catch (DatoInvalidoException e) {
            lblResultado.setText(e.getMessage());
        }
    }

    @FXML
    void eliminarCliente() {
        if (selectedCliente == null) {
            lblResultado.setText("Seleccione un cliente de la tabla.");
            return;
        }
        try {
            clienteController.eliminarCliente(selectedCliente.getDocumento());
            actualizarTabla();
            limpiarCampos();
            lblResultado.setText("Cliente eliminado.");
        } catch (DatoInvalidoException | CuentaNoEncontradaException e) {
            lblResultado.setText(e.getMessage());
        }
    }

    @FXML
    void limpiarCampos() {
        tblClientes.getSelectionModel().clearSelection();
        selectedCliente = null;
        txtDocumento.clear();
        txtNombre.clear();
        txtDocumento.setDisable(false);
    }

    public void actualizarTabla() {
        listaClientes.setAll(clienteController.obtenerClientes());
        tblClientes.refresh();
        if (selectedCliente != null && !listaClientes.contains(selectedCliente)) limpiarCampos();
    }
}
