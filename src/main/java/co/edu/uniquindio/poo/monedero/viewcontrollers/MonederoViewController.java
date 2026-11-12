package co.edu.uniquindio.poo.monedero.viewcontrollers;

import co.edu.uniquindio.poo.monedero.controller.MonederoController;
import co.edu.uniquindio.poo.monedero.app.Aplicacion;
import javafx.event.ActionEvent;
import co.edu.uniquindio.poo.monedero.model.Cliente;
import co.edu.uniquindio.poo.monedero.model.Monedero;
import co.edu.uniquindio.poo.monedero.model.MovimientoPuntos;
import co.edu.uniquindio.poo.monedero.model.Retiro;
import co.edu.uniquindio.poo.monedero.model.Transaccion;
import co.edu.uniquindio.poo.monedero.model.TransaccionProgramada;
import co.edu.uniquindio.poo.monedero.model.Transferencia;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.time.LocalDate;

public class MonederoViewController {
    private Aplicacion aplicacion;
    private MonederoController monederoController;
    @FXML private TextField txtNombreMonedero, txtMonto, txtCodigo, txtCodigoProgramada;
    @FXML private ComboBox<Cliente> cmbCliente, cmbDestinatario;
    @FXML private ComboBox<Monedero> cmbMonedero, cmbMonederoDestino;
    @FXML private ComboBox<String> cmbFrecuencia, cmbTipoProgramada;
    @FXML private DatePicker dpFecha;
    @FXML private TextArea txtSalida;
    @FXML private Label lblResumen;

    @FXML public void initialize() {
        dpFecha.setValue(LocalDate.now());
        cmbFrecuencia.setItems(FXCollections.observableArrayList("Única", "Semanal", "Mensual"));
        cmbFrecuencia.getSelectionModel().selectFirst();
        cmbTipoProgramada.setItems(FXCollections.observableArrayList("Depósito", "Transferencia"));
        cmbTipoProgramada.getSelectionModel().selectFirst();
        cmbCliente.setOnAction(event -> actualizarMonederos());
        cmbDestinatario.setOnAction(event -> actualizarDestino());
        txtSalida.setText("Selecciona un cliente registrado en la pantalla Clientes.\nLas operaciones usan el cliente y el monedero seleccionados arriba.");
    }

    public void setApp(Aplicacion aplicacion) {
        this.aplicacion = aplicacion;
        this.monederoController = new MonederoController(Aplicacion.monederoVirtual);
        refrescar();
    }

    @FXML
    void volverEvent(ActionEvent event) {
        aplicacion.openViewInicio();
    }

    private void refrescar() {
        Cliente actual = cmbCliente.getValue();
        Cliente receptor = cmbDestinatario.getValue();
        if (!monederoController.obtenerClientes().contains(actual)) actual = null;
        if (!monederoController.obtenerClientes().contains(receptor)) receptor = null;
        Monedero actualMonedero = cmbMonedero.getValue();
        Monedero receptorMonedero = cmbMonederoDestino.getValue();
        cmbCliente.setItems(FXCollections.observableArrayList(monederoController.obtenerClientes()));
        cmbDestinatario.setItems(FXCollections.observableArrayList(monederoController.obtenerClientes()));
        cmbCliente.setValue(actual);
        cmbDestinatario.setValue(receptor);
        actualizarMonederos();
        actualizarDestino();
        if (actual != null && actual.getMonederos().contains(actualMonedero)) cmbMonedero.setValue(actualMonedero);
        if (receptor != null && receptor.getMonederos().contains(receptorMonedero)) cmbMonederoDestino.setValue(receptorMonedero);
        actualizarResumen();
    }

    private void actualizarMonederos() {
        Cliente c = cmbCliente.getValue();
        if (c == null) {
            cmbMonedero.setItems(FXCollections.observableArrayList());
            cmbMonedero.setValue(null);
        }
        else {
            cmbMonedero.setItems(FXCollections.observableArrayList(c.getMonederos()));
            cmbMonedero.getSelectionModel().selectFirst();
        }
        actualizarResumen();
    }

    private void actualizarDestino() {
        Cliente c = cmbDestinatario.getValue();
        if (c == null) {
            cmbMonederoDestino.setItems(FXCollections.observableArrayList());
            cmbMonederoDestino.setValue(null);
        }
        else {
            cmbMonederoDestino.setItems(FXCollections.observableArrayList(c.getMonederos()));
            cmbMonederoDestino.getSelectionModel().selectFirst();
        }
    }

    private void actualizarResumen() {
        Cliente c = cmbCliente.getValue();
        if (c == null) { lblResumen.setText("Selecciona un cliente para comenzar."); return; }
        double total = 0;
        for (Monedero m : c.getMonederos()) total += m.getSaldo();
        lblResumen.setText("Saldo total: $" + String.format("%.2f", total) +
            "     Puntos: " + c.getPuntos() + "     Rango: " + c.getRango());
    }

    private Cliente clienteActivo() {
        return cmbCliente.getValue();
    }

    private Monedero monederoActivo() {
        return cmbMonedero.getValue();
    }

    private double leerMonto() {
        return Double.parseDouble(txtMonto.getText().trim());
    }

    private int leerCodigo(TextField campo) {
        return Integer.parseInt(campo.getText().trim());
    }

    private void mensajeError(Exception e) {
        if (e instanceof NumberFormatException) txtSalida.setText("Ingresa un número válido para el monto o código.");
        else txtSalida.setText("Aviso: " + e.getMessage());
    }

    @FXML
    void registrarMonederoEvent(ActionEvent event) {
        try {
            Monedero nuevo = monederoController.registrarMonedero(clienteActivo(), txtNombreMonedero.getText());
            refrescar(); cmbMonedero.setValue(nuevo); txtSalida.setText("Monedero creado: " + nuevo);
        } catch (Exception e) { mensajeError(e); }
    }

    @FXML
    void depositarEvent(ActionEvent event) {
        try {
            Transaccion t = monederoController.depositar(clienteActivo(), monederoActivo(), leerMonto());
            refrescar(); txtSalida.setText("Depósito realizado.\n" + t);
        } catch (Exception e) { mensajeError(e); }
    }

    @FXML
    void retirarEvent(ActionEvent event) {
        try {
            Transaccion t = monederoController.retirar(clienteActivo(), monederoActivo(), leerMonto());
            refrescar(); txtSalida.setText("Retiro realizado.\n" + t);
        } catch (Exception e) { mensajeError(e); }
    }

    @FXML
    void transferirEvent(ActionEvent event) {
        try {
            Transaccion t = monederoController.transferir(clienteActivo(), monederoActivo(), cmbDestinatario.getValue(), cmbMonederoDestino.getValue(), leerMonto());
            refrescar(); txtSalida.setText("Transferencia realizada.\n" + t);
        } catch (Exception e) { mensajeError(e); }
    }

    @FXML
    void historialEvent(ActionEvent event) {
        Cliente c = clienteActivo();
        if (c == null) { txtSalida.setText("Selecciona un cliente."); return; }
        String texto = "HISTORIAL DE " + c.getNombre() + "\n";
        for (Transaccion t : c.getHistorial()) texto += t + "\n";
        txtSalida.setText(texto);
    }

    @FXML
    void historialPuntosEvent(ActionEvent event) {
        Cliente c = clienteActivo();
        if (c == null) { txtSalida.setText("Selecciona un cliente."); return; }
        String texto = "MOVIMIENTOS DE PUNTOS\n";
        for (MovimientoPuntos m : c.getMovimientosPuntos()) texto += m + "\n";
        txtSalida.setText(texto);
    }

    @FXML
    void revertirEvent(ActionEvent event) {
        try {
            monederoController.revertir(clienteActivo(), leerCodigo(txtCodigo));
            refrescar(); txtSalida.setText("Transacción revertida y puntos recalculados.");
        } catch (Exception e) { mensajeError(e); }
    }

    @FXML
    void reporteEvent(ActionEvent event) {
        try { txtSalida.setText(monederoController.reporte(clienteActivo())); }
        catch (Exception e) { mensajeError(e); }
    }

    @FXML
    void canjear100Event(ActionEvent event) {
        canjear(100);
    }

    @FXML
    void canjear500Event(ActionEvent event) {
        canjear(500);
    }

    @FXML
    void canjear1000Event(ActionEvent event) {
        canjear(1000);
    }

    private void canjear(int puntos) {
        try {
            monederoController.canjear(clienteActivo(), monederoActivo(), puntos);
            refrescar(); txtSalida.setText("Beneficio canjeado por " + puntos + " puntos.");
        } catch (Exception e) { mensajeError(e); }
    }

    @FXML
    void programarEvent(ActionEvent event) {
        try {
            TransaccionProgramada p = monederoController.programar(cmbTipoProgramada.getValue(), clienteActivo(),
                monederoActivo(), cmbDestinatario.getValue(), cmbMonederoDestino.getValue(), leerMonto(),
                dpFecha.getValue(), cmbFrecuencia.getValue());
            txtSalida.setText("Programación guardada: " + p);
        } catch (Exception e) { mensajeError(e); }
    }

    @FXML
    void ejecutarProgramadasEvent(ActionEvent event) {
        txtSalida.setText(monederoController.ejecutarPendientes(LocalDate.now())); refrescar();
    }

    @FXML
    void verProgramadasEvent(ActionEvent event) {
        String texto = "OPERACIONES PROGRAMADAS\n";
        for (TransaccionProgramada p : monederoController.obtenerProgramadas()) texto += p + "\n";
        txtSalida.setText(texto);
    }

    @FXML
    void cancelarProgramadaEvent(ActionEvent event) {
        try {
            monederoController.cancelarProgramada(leerCodigo(txtCodigoProgramada));
            txtSalida.setText("Operación programada cancelada.");
        } catch (Exception e) { mensajeError(e); }
    }

    @FXML
    void verNotificacionesEvent(ActionEvent event) {
        String texto = "NOTIFICACIONES DEL SIMULADOR\n";
        for (String aviso : monederoController.obtenerNotificaciones()) texto += aviso + "\n";
        txtSalida.setText(texto);
    }
}
