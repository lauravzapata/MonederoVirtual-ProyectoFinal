package co.edu.uniquindio.poo.monedero.viewcontrollers;

import co.edu.uniquindio.poo.monedero.controller.MonederoController;
import co.edu.uniquindio.poo.monedero.app.Aplicacion;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import co.edu.uniquindio.poo.monedero.model.Cliente;
import co.edu.uniquindio.poo.monedero.exceptions.DatoInvalidoException;
import co.edu.uniquindio.poo.monedero.exceptions.SaldoInsuficienteException;
import co.edu.uniquindio.poo.monedero.exceptions.CuentaNoEncontradaException;
import co.edu.uniquindio.poo.monedero.model.Monedero;
import co.edu.uniquindio.poo.monedero.model.MovimientoPuntos;
import co.edu.uniquindio.poo.monedero.model.Transaccion;
import co.edu.uniquindio.poo.monedero.model.TransaccionProgramada;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.time.LocalDate;

public class MonederoViewController {
    private final ObservableList<Transaccion> listaTransacciones = FXCollections.observableArrayList();
    private MonederoController monederoController;
    @FXML private TextField txtNombreMonedero, txtMonto, txtCodigo, txtCodigoProgramada;
    @FXML private ComboBox<Cliente> cmbCliente, cmbDestinatario;
    @FXML private ComboBox<Monedero> cmbMonedero, cmbMonederoDestino;
    @FXML private ComboBox<String> cmbFrecuencia, cmbTipoProgramada;
    @FXML private DatePicker dpFecha;
    @FXML private TextArea txtSalida;
    @FXML private Label lblSaldo;
    @FXML private Label lblPuntos;
    @FXML private Label lblRango;
    @FXML private TableView<Transaccion> tblTransacciones;
    @FXML private TableColumn<Transaccion, String> tbcCodigo;
    @FXML private TableColumn<Transaccion, String> tbcFecha;
    @FXML private TableColumn<Transaccion, String> tbcTipo;
    @FXML private TableColumn<Transaccion, String> tbcMonto;
    @FXML private TableColumn<Transaccion, String> tbcComision;
    @FXML private TableColumn<Transaccion, String> tbcEstado;

    @FXML
    void initialize() {
        monederoController = new MonederoController(Aplicacion.monederoVirtual);
        initDataBinding();
        tblTransacciones.setItems(listaTransacciones);
        tblTransacciones.getSelectionModel().selectedItemProperty().addListener((obs, anterior, actual) -> {
            if (actual != null) txtCodigo.setText(String.valueOf(actual.getCodigo()));
        });
        dpFecha.setValue(LocalDate.now());
        cmbFrecuencia.setItems(FXCollections.observableArrayList("Única", "Semanal", "Mensual"));
        cmbFrecuencia.getSelectionModel().selectFirst();
        cmbTipoProgramada.setItems(FXCollections.observableArrayList("Depósito", "Transferencia"));
        cmbTipoProgramada.getSelectionModel().selectFirst();
        cmbCliente.setOnAction(event -> actualizarMonederos());
        cmbDestinatario.setOnAction(event -> actualizarDestino());
        cmbMonedero.setOnAction(event -> actualizarResumen());
        actualizarDatos();
    }

    public void actualizarDatos() {
        refrescar();
        if (monederoController.obtenerClientes().isEmpty()) {
            txtSalida.setText("Registre un cliente en Clientes para realizar un depósito.");
        } else if (txtSalida.getText().startsWith("Registre")) {
            txtSalida.clear();
        }
    }

    private void initDataBinding() {
        tbcCodigo.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getCodigo())));
        tbcFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFecha().toLocalDate().toString()));
        tbcTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo()));
        tbcMonto.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getMonto())));
        tbcComision.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getComision())));
        tbcEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isRevertida() ? "Revertida" :
                c.getValue().isSospechosa() ? "Atípica" : "Realizada"));
    }

    private void cargarTransacciones() {
        Cliente cliente = clienteActivo();
        listaTransacciones.clear();
        if (cliente != null) listaTransacciones.addAll(cliente.getHistorial());
        tblTransacciones.refresh();
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
        if (actual == null && !monederoController.obtenerClientes().isEmpty())
            actual = monederoController.obtenerClientes().get(0);
        cmbCliente.setValue(actual);
        cmbDestinatario.setValue(receptor);
        actualizarMonederos();
        actualizarDestino();
        if (actual != null && actual.getMonederos().contains(actualMonedero)) cmbMonedero.setValue(actualMonedero);
        if (receptor != null && receptor.getMonederos().contains(receptorMonedero)) cmbMonederoDestino.setValue(receptorMonedero);
        actualizarResumen();
        cargarTransacciones();
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
        cargarTransacciones();
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
        Cliente cliente = cmbCliente.getValue();
        Monedero monedero = cmbMonedero.getValue();
        lblSaldo.setText("Saldo: $" + String.format("%.2f", monedero == null ? 0 : monedero.getSaldo()));
        lblPuntos.setText("Puntos: " + (cliente == null ? 0 : cliente.getPuntos()));
        lblRango.setText("Rango: " + (cliente == null ? "-" : cliente.getRango()));
    }

    private Cliente clienteActivo() {
        return cmbCliente.getValue();
    }

    private Monedero monederoActivo() {
        return cmbMonedero.getValue();
    }

    private double leerMonto() {
        return Double.parseDouble(txtMonto.getText().trim().replace(',', '.'));
    }

    private int leerCodigo(TextField campo) {
        return Integer.parseInt(campo.getText().trim());
    }

    private void mensajeError(Exception e) {
        if (e instanceof NumberFormatException) txtSalida.setText("Ingresa un número válido para el monto o código.");
        else txtSalida.setText("Aviso: " + e.getMessage());
    }

    @FXML
    void registrarMonedero() {
        try {
            Monedero nuevo = monederoController.registrarMonedero(clienteActivo(), txtNombreMonedero.getText());
            refrescar(); cmbMonedero.setValue(nuevo); txtSalida.setText("Monedero creado: " + nuevo);
        } catch (DatoInvalidoException e) { mensajeError(e); }
    }

    @FXML
    void depositar() {
        if (clienteActivo() == null || monederoActivo() == null) {
            txtSalida.setText("Registre un cliente y seleccione un monedero para depositar dinero.");
            return;
        }
        try {
            Transaccion t = monederoController.depositar(clienteActivo(), monederoActivo(), leerMonto());
            refrescar();
            txtSalida.setText("Depósito realizado. Saldo: $" + String.format("%.2f", monederoActivo().getSaldo()));
        } catch (DatoInvalidoException | NumberFormatException e) { mensajeError(e); }
    }

    @FXML
    void retirar() {
        try {
            Transaccion t = monederoController.retirar(clienteActivo(), monederoActivo(), leerMonto());
            refrescar(); txtSalida.setText("Retiro realizado.\n" + t);
        } catch (DatoInvalidoException | SaldoInsuficienteException | NumberFormatException e) { mensajeError(e); }
    }

    @FXML
    void transferir() {
        try {
            Transaccion t = monederoController.transferir(clienteActivo(), monederoActivo(), cmbDestinatario.getValue(), cmbMonederoDestino.getValue(), leerMonto());
            refrescar(); txtSalida.setText("Transferencia realizada.\n" + t);
        } catch (DatoInvalidoException | SaldoInsuficienteException | NumberFormatException e) { mensajeError(e); }
    }

    @FXML
    void historial() {
        Cliente c = clienteActivo();
        if (c == null) { txtSalida.setText("Selecciona un cliente."); return; }
        String texto = "HISTORIAL DE " + c.getNombre() + "\n";
        for (Transaccion t : c.getHistorial()) texto += t + "\n";
        txtSalida.setText(texto);
    }

    @FXML
    void historialPuntos() {
        Cliente c = clienteActivo();
        if (c == null) { txtSalida.setText("Selecciona un cliente."); return; }
        String texto = "MOVIMIENTOS DE PUNTOS\n";
        for (MovimientoPuntos m : c.getMovimientosPuntos()) texto += m + "\n";
        txtSalida.setText(texto);
    }

    @FXML
    void revertir() {
        try {
            monederoController.revertir(clienteActivo(), leerCodigo(txtCodigo));
            refrescar(); txtSalida.setText("Transacción revertida y puntos recalculados.");
        } catch (DatoInvalidoException | SaldoInsuficienteException | CuentaNoEncontradaException | NumberFormatException e) { mensajeError(e); }
    }

    @FXML
    void reporte() {
        try { txtSalida.setText(monederoController.reporte(clienteActivo())); }
        catch (DatoInvalidoException e) { mensajeError(e); }
    }

    @FXML
    void canjear100() {
        canjear(100);
    }

    @FXML
    void canjear500() {
        canjear(500);
    }

    @FXML
    void canjear1000() {
        canjear(1000);
    }

    private void canjear(int puntos) {
        try {
            monederoController.canjear(clienteActivo(), monederoActivo(), puntos);
            refrescar(); txtSalida.setText("Beneficio canjeado por " + puntos + " puntos.");
        } catch (DatoInvalidoException e) { mensajeError(e); }
    }

    @FXML
    void programar() {
        try {
            TransaccionProgramada p = monederoController.programar(cmbTipoProgramada.getValue(), clienteActivo(),
                monederoActivo(), cmbDestinatario.getValue(), cmbMonederoDestino.getValue(), leerMonto(),
                dpFecha.getValue(), cmbFrecuencia.getValue());
            txtSalida.setText("Programación guardada: " + p);
        } catch (DatoInvalidoException | NumberFormatException e) { mensajeError(e); }
    }

    @FXML
    void ejecutarProgramadas() {
        txtSalida.setText(monederoController.ejecutarPendientes(LocalDate.now())); refrescar();
    }

    @FXML
    void verProgramadas() {
        String texto = "OPERACIONES PROGRAMADAS\n";
        for (TransaccionProgramada p : monederoController.obtenerProgramadas()) texto += p + "\n";
        txtSalida.setText(texto);
    }

    @FXML
    void cancelarProgramada() {
        try {
            monederoController.cancelarProgramada(leerCodigo(txtCodigoProgramada));
            txtSalida.setText("Operación programada cancelada.");
        } catch (CuentaNoEncontradaException | NumberFormatException e) { mensajeError(e); }
    }

    @FXML
    void verNotificaciones() {
        String texto = "NOTIFICACIONES\n";
        for (String aviso : monederoController.obtenerNotificaciones()) texto += aviso + "\n";
        txtSalida.setText(texto);
    }
}
