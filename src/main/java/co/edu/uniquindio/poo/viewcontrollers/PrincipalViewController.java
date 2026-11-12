package co.edu.uniquindio.poo.viewcontrollers;

import co.edu.uniquindio.poo.controller.MonederoController;
import co.edu.uniquindio.poo.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class PrincipalViewController {
    private MonederoController controller = new MonederoController();
    @FXML private TextField documento, nombre, nombreMonedero, monto, codigo, codigoProgramada;
    @FXML private ComboBox<Cliente> cliente, destinatario;
    @FXML private ComboBox<Monedero> monedero, monederoDestino;
    @FXML private ComboBox<String> frecuencia, tipoProgramada;
    @FXML private DatePicker fecha;
    @FXML private TextArea salida;
    @FXML private Label resumen;

    @FXML public void initialize() {
        fecha.setValue(LocalDate.now());
        frecuencia.setItems(FXCollections.observableArrayList("Única", "Semanal", "Mensual"));
        frecuencia.getSelectionModel().selectFirst();
        tipoProgramada.setItems(FXCollections.observableArrayList("Depósito", "Transferencia"));
        tipoProgramada.getSelectionModel().selectFirst();
        cliente.setOnAction(event -> actualizarMonederos());
        destinatario.setOnAction(event -> actualizarDestino());
        refrescar();
        salida.setText("¡Bienvenida! Registra un cliente y realiza tu primer depósito.\nLas operaciones usan el cliente y el monedero seleccionados arriba.");
    }
    private void refrescar() {
        Cliente actual = cliente.getValue();
        Cliente receptor = destinatario.getValue();
        if (!controller.getClientes().contains(actual)) actual = null;
        if (!controller.getClientes().contains(receptor)) receptor = null;
        Monedero actualMonedero = monedero.getValue();
        Monedero receptorMonedero = monederoDestino.getValue();
        cliente.setItems(FXCollections.observableArrayList(controller.getClientes()));
        destinatario.setItems(FXCollections.observableArrayList(controller.getClientes()));
        cliente.setValue(actual);
        destinatario.setValue(receptor);
        actualizarMonederos();
        actualizarDestino();
        if (actual != null && actual.getMonederos().contains(actualMonedero)) monedero.setValue(actualMonedero);
        if (receptor != null && receptor.getMonederos().contains(receptorMonedero)) monederoDestino.setValue(receptorMonedero);
        actualizarResumen();
    }
    private void actualizarMonederos() {
        Cliente c = cliente.getValue();
        if (c == null) {
            monedero.setItems(FXCollections.observableArrayList());
            monedero.setValue(null);
        }
        else {
            monedero.setItems(FXCollections.observableArrayList(c.getMonederos()));
            monedero.getSelectionModel().selectFirst();
        }
        actualizarResumen();
    }
    private void actualizarDestino() {
        Cliente c = destinatario.getValue();
        if (c == null) {
            monederoDestino.setItems(FXCollections.observableArrayList());
            monederoDestino.setValue(null);
        }
        else {
            monederoDestino.setItems(FXCollections.observableArrayList(c.getMonederos()));
            monederoDestino.getSelectionModel().selectFirst();
        }
    }
    private void actualizarResumen() {
        Cliente c = cliente.getValue();
        if (c == null) { resumen.setText("Selecciona un cliente para comenzar."); return; }
        double total = 0;
        for (Monedero m : c.getMonederos()) total += m.getSaldo();
        resumen.setText("Saldo total: $" + String.format("%.2f", total) +
            "     Puntos: " + c.getPuntos() + "     Rango: " + c.getRango());
    }
    private Cliente clienteActivo() { return cliente.getValue(); }
    private Monedero monederoActivo() { return monedero.getValue(); }
    private double leerMonto() { return Double.parseDouble(monto.getText().trim()); }
    private int leerCodigo(TextField campo) { return Integer.parseInt(campo.getText().trim()); }
    private void mensajeError(Exception e) {
        if (e instanceof NumberFormatException) salida.setText("Ingresa un número válido para el monto o código.");
        else salida.setText("Aviso: " + e.getMessage());
    }
    @FXML private void registrarCliente() {
        try {
            Cliente nuevo = controller.registrarCliente(documento.getText(), nombre.getText());
            refrescar(); cliente.setValue(nuevo); actualizarMonederos();
            salida.setText("Cliente registrado: " + nuevo);
        } catch (Exception e) { mensajeError(e); }
    }
    @FXML private void editarCliente() {
        try {
            controller.editarNombre(clienteActivo(), nombre.getText());
            refrescar(); salida.setText("Nombre actualizado correctamente.");
        } catch (Exception e) { mensajeError(e); }
    }
    @FXML private void eliminarCliente() {
        try {
            Cliente c = clienteActivo();
            if (c == null) { salida.setText("Selecciona un cliente."); return; }
            Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                    "¿Eliminar al cliente " + c.getNombre() + "?", ButtonType.OK, ButtonType.CANCEL);
            confirmar.setHeaderText("Eliminar cliente");
            if (confirmar.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
            controller.eliminarCliente(c.getDocumento());
            cliente.setValue(null); refrescar(); salida.setText("Cliente eliminado.");
        } catch (Exception e) { mensajeError(e); }
    }
    @FXML private void registrarMonedero() {
        try {
            Monedero nuevo = controller.registrarMonedero(clienteActivo(), nombreMonedero.getText());
            refrescar(); monedero.setValue(nuevo); salida.setText("Monedero creado: " + nuevo);
        } catch (Exception e) { mensajeError(e); }
    }
    @FXML private void depositar() {
        try {
            Transaccion t = controller.depositar(clienteActivo(), monederoActivo(), leerMonto());
            refrescar(); salida.setText("Depósito realizado.\n" + t);
        } catch (Exception e) { mensajeError(e); }
    }
    @FXML private void retirar() {
        try {
            Transaccion t = controller.retirar(clienteActivo(), monederoActivo(), leerMonto());
            refrescar(); salida.setText("Retiro realizado.\n" + t);
        } catch (Exception e) { mensajeError(e); }
    }
    @FXML private void transferir() {
        try {
            Transaccion t = controller.transferir(clienteActivo(), monederoActivo(), destinatario.getValue(), monederoDestino.getValue(), leerMonto());
            refrescar(); salida.setText("Transferencia realizada.\n" + t);
        } catch (Exception e) { mensajeError(e); }
    }
    @FXML private void historial() {
        Cliente c = clienteActivo();
        if (c == null) { salida.setText("Selecciona un cliente."); return; }
        String texto = "HISTORIAL DE " + c.getNombre() + "\n";
        for (Transaccion t : c.getHistorial()) texto += t + "\n";
        salida.setText(texto);
    }
    @FXML private void historialPuntos() {
        Cliente c = clienteActivo();
        if (c == null) { salida.setText("Selecciona un cliente."); return; }
        String texto = "MOVIMIENTOS DE PUNTOS\n";
        for (MovimientoPuntos m : c.getMovimientosPuntos()) texto += m + "\n";
        salida.setText(texto);
    }
    @FXML private void revertir() {
        try {
            controller.revertir(clienteActivo(), leerCodigo(codigo));
            refrescar(); salida.setText("Transacción revertida y puntos recalculados.");
        } catch (Exception e) { mensajeError(e); }
    }
    @FXML private void reporte() {
        try { salida.setText(controller.reporte(clienteActivo())); }
        catch (Exception e) { mensajeError(e); }
    }
    @FXML private void canjear100() { canjear(100); }
    @FXML private void canjear500() { canjear(500); }
    @FXML private void canjear1000() { canjear(1000); }
    private void canjear(int puntos) {
        try {
            controller.canjear(clienteActivo(), monederoActivo(), puntos);
            refrescar(); salida.setText("Beneficio canjeado por " + puntos + " puntos.");
        } catch (Exception e) { mensajeError(e); }
    }
    @FXML private void programar() {
        try {
            TransaccionProgramada p = controller.programar(tipoProgramada.getValue(), clienteActivo(),
                monederoActivo(), destinatario.getValue(), monederoDestino.getValue(), leerMonto(),
                fecha.getValue(), frecuencia.getValue());
            salida.setText("Programación guardada: " + p);
        } catch (Exception e) { mensajeError(e); }
    }
    @FXML private void ejecutarProgramadas() {
        salida.setText(controller.ejecutarPendientes(LocalDate.now())); refrescar();
    }
    @FXML private void verProgramadas() {
        String texto = "OPERACIONES PROGRAMADAS\n";
        for (TransaccionProgramada p : controller.getProgramadas()) texto += p + "\n";
        salida.setText(texto);
    }
    @FXML private void cancelarProgramada() {
        try {
            controller.cancelarProgramada(leerCodigo(codigoProgramada));
            salida.setText("Operación programada cancelada.");
        } catch (Exception e) { mensajeError(e); }
    }
    @FXML private void verNotificaciones() {
        String texto = "NOTIFICACIONES DEL SIMULADOR\n";
        for (String aviso : controller.getNotificaciones()) texto += aviso + "\n";
        salida.setText(texto);
    }
}
