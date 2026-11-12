# Códigos modificados

Cinco archivos Java y cuatro FXML completos. Conserva las rutas indicadas.

## src/main/java/co/edu/uniquindio/poo/monedero/app/Aplicacion.java

```java
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
```

## src/main/java/co/edu/uniquindio/poo/monedero/viewcontrollers/ClienteViewController.java

```java
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
```

## src/main/java/co/edu/uniquindio/poo/monedero/viewcontrollers/GestionViewController.java

```java
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
```

## src/main/java/co/edu/uniquindio/poo/monedero/viewcontrollers/InicioViewController.java

```java
package co.edu.uniquindio.poo.monedero.viewcontrollers;

import co.edu.uniquindio.poo.monedero.app.Aplicacion;
import javafx.fxml.FXML;

public class InicioViewController {
    private Aplicacion aplicacion;

    public void setApp(Aplicacion aplicacion) {
        this.aplicacion = aplicacion;
    }

    @FXML
    void abrirClientes() {
        aplicacion.openViewClientes();
    }

    @FXML
    void abrirMonedero() {
        aplicacion.openViewMonedero();
    }
}
```

## src/main/java/co/edu/uniquindio/poo/monedero/viewcontrollers/MonederoViewController.java

```java
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
```

## src/main/resources/co/edu/uniquindio/poo/monedero/clientes.fxml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.text.Font?>
<AnchorPane xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1" fx:controller="co.edu.uniquindio.poo.monedero.viewcontrollers.ClienteViewController" prefWidth="1080" prefHeight="720">
    <children>
        <Label text="Gestión de clientes" layoutX="24" layoutY="20" textFill="#315e82">
            <font>
                <Font name="System Bold" size="20"/>
            </font>
        </Label>
        <Label text="Documento" layoutX="24" layoutY="70" textFill="#334155">
            <font>
                <Font name="System" size="13"/>
            </font>
        </Label>
        <Label text="Nombre" layoutX="300" layoutY="70" textFill="#334155">
            <font>
                <Font name="System" size="13"/>
            </font>
        </Label>
        <TextField fx:id="txtDocumento" layoutX="24" layoutY="96" prefWidth="240" prefHeight="34" promptText=""/>
        <TextField fx:id="txtNombre" layoutX="300" layoutY="96" prefWidth="420" prefHeight="34" promptText=""/>
        <Button text="Registrar cliente" onAction="#registrarCliente" layoutX="24" layoutY="155" prefWidth="175" prefHeight="34"/>
        <Button text="Actualizar cliente" onAction="#actualizarCliente" layoutX="215" layoutY="155" prefWidth="175" prefHeight="34"/>
        <Button text="Eliminar cliente" onAction="#eliminarCliente" layoutX="405" layoutY="155" prefWidth="175" prefHeight="34"/>
        <Button text="Limpiar" onAction="#limpiarCampos" layoutX="595" layoutY="155" prefWidth="125" prefHeight="34"/>
        <Label text="Clientes registrados" layoutX="24" layoutY="215" textFill="#334155">
            <font>
                <Font name="System Bold" size="16"/>
            </font>
        </Label>
        <TableView fx:id="tblClientes" layoutX="24" layoutY="250" AnchorPane.leftAnchor="24" AnchorPane.rightAnchor="24" AnchorPane.bottomAnchor="110">
            <placeholder><Label text="Sin clientes registrados"/></placeholder>
            <columns>
                <TableColumn fx:id="tbcDocumento" text="Documento" prefWidth="200"/>
                <TableColumn fx:id="tbcNombre" text="Nombre" prefWidth="440"/>
                <TableColumn fx:id="tbcPuntos" text="Puntos" prefWidth="150"/>
                <TableColumn fx:id="tbcRango" text="Rango" prefWidth="180"/>
            </columns>
        </TableView>
        <Label fx:id="lblResultado" text="Seleccione un cliente de la tabla para actualizarlo o eliminarlo." wrapText="true" AnchorPane.leftAnchor="24" AnchorPane.rightAnchor="24" AnchorPane.bottomAnchor="50" textFill="#315e82"/>
    </children>
</AnchorPane>
```

## src/main/resources/co/edu/uniquindio/poo/monedero/gestion.fxml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.text.Font?>
<AnchorPane xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1" fx:controller="co.edu.uniquindio.poo.monedero.viewcontrollers.GestionViewController" prefWidth="1120" prefHeight="840">
    <children>
        <Label text="Monedero virtual" layoutX="24" layoutY="18" textFill="#315e82">
            <font>
                <Font name="System Bold" size="24"/>
            </font>
        </Label>
        <Button text="Inicio" onAction="#volver" layoutX="925" layoutY="18" prefWidth="140" prefHeight="34"/>
        <TabPane fx:id="tabPane" tabClosingPolicy="UNAVAILABLE" AnchorPane.topAnchor="75" AnchorPane.bottomAnchor="15" AnchorPane.leftAnchor="20" AnchorPane.rightAnchor="20">
            <tabs>
                <Tab fx:id="tabClientes" text="Clientes"/>
                <Tab fx:id="tabMonedero" text="Monedero"/>
            </tabs>
        </TabPane>
    </children>
</AnchorPane>
```

## src/main/resources/co/edu/uniquindio/poo/monedero/inicio.fxml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.text.Font?>
<AnchorPane xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1" fx:controller="co.edu.uniquindio.poo.monedero.viewcontrollers.InicioViewController" prefWidth="1120" prefHeight="840">
    <children>
        <Label text="Monedero virtual" layoutX="70" layoutY="90" textFill="#315e82">
            <font>
                <Font name="System Bold" size="30"/>
            </font>
        </Label>
        <Separator layoutX="70" layoutY="155" prefWidth="920"/>
        <Label text="Clientes" layoutX="70" layoutY="210" textFill="#334155">
            <font>
                <Font name="System Bold" size="20"/>
            </font>
        </Label>
        <Label text="Depósitos y operaciones" layoutX="500" layoutY="210" textFill="#334155">
            <font>
                <Font name="System Bold" size="20"/>
            </font>
        </Label>
        <Button text="Clientes" onAction="#abrirClientes" layoutX="70" layoutY="265" prefWidth="300" prefHeight="34"/>
        <Button text="Depositar dinero" onAction="#abrirMonedero" layoutX="500" layoutY="265" prefWidth="300" prefHeight="34"/>
        <Button text="Monedero" onAction="#abrirMonedero" layoutX="500" layoutY="325" prefWidth="300" prefHeight="34"/>
        <Label text="Seleccione una opción." layoutX="70" layoutY="420" textFill="#334155">
            <font>
                <Font name="System" size="14"/>
            </font>
        </Label>
    </children>
</AnchorPane>
```

## src/main/resources/co/edu/uniquindio/poo/monedero/monedero.fxml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.text.Font?>
<AnchorPane xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1" fx:controller="co.edu.uniquindio.poo.monedero.viewcontrollers.MonederoViewController" prefWidth="1080" prefHeight="720">
    <children>
        <Label text="Cliente" layoutX="24" layoutY="12" textFill="#334155">
            <font>
                <Font name="System" size="13"/>
            </font>
        </Label>
        <Label text="Monedero" layoutX="320" layoutY="12" textFill="#334155">
            <font>
                <Font name="System" size="13"/>
            </font>
        </Label>
        <ComboBox fx:id="cmbCliente" layoutX="24" layoutY="36" prefWidth="270" prefHeight="34" promptText="Seleccione un cliente"/>
        <ComboBox fx:id="cmbMonedero" layoutX="320" layoutY="36" prefWidth="270" prefHeight="34" promptText="Seleccione un monedero"/>
        <Label text="Saldo: $0.00" layoutX="630" layoutY="40" textFill="#315e82" fx:id="lblSaldo">
            <font>
                <Font name="System Bold" size="20"/>
            </font>
        </Label>
        <Label text="Puntos: 0" layoutX="630" layoutY="82" textFill="#334155" fx:id="lblPuntos">
            <font>
                <Font name="System" size="14"/>
            </font>
        </Label>
        <Label text="Rango: -" layoutX="810" layoutY="82" textFill="#334155" fx:id="lblRango">
            <font>
                <Font name="System" size="14"/>
            </font>
        </Label>
        <TextField fx:id="txtNombreMonedero" layoutX="24" layoutY="84" prefWidth="270" prefHeight="34" promptText="Nombre de nuevo monedero"/>
        <Button text="Crear monedero" onAction="#registrarMonedero" layoutX="320" layoutY="84" prefWidth="200" prefHeight="34"/>
        <Separator layoutX="24" layoutY="135" prefWidth="1000"/>
        <Label text="Depósitos y retiros" layoutX="24" layoutY="150" textFill="#315e82">
            <font>
                <Font name="System Bold" size="18"/>
            </font>
        </Label>
        <Label text="Monto" layoutX="24" layoutY="197" textFill="#334155">
            <font>
                <Font name="System" size="13"/>
            </font>
        </Label>
        <TextField fx:id="txtMonto" layoutX="90" layoutY="190" prefWidth="190" prefHeight="34" promptText="Ejemplo: 1000"/>
        <Button text="Depositar dinero" onAction="#depositar" layoutX="300" layoutY="190" prefWidth="185" prefHeight="34"/>
        <Button text="Retirar" onAction="#retirar" layoutX="505" layoutY="190" prefWidth="140" prefHeight="34"/>
        <Label text="Transferencias" layoutX="24" layoutY="240" textFill="#334155">
            <font>
                <Font name="System Bold" size="16"/>
            </font>
        </Label>
        <ComboBox fx:id="cmbDestinatario" layoutX="24" layoutY="270" prefWidth="270" prefHeight="34" promptText="Cliente destino"/>
        <ComboBox fx:id="cmbMonederoDestino" layoutX="320" layoutY="270" prefWidth="270" prefHeight="34" promptText="Monedero destino"/>
        <Button text="Transferir" onAction="#transferir" layoutX="620" layoutY="270" prefWidth="170" prefHeight="34"/>
        <TabPane fx:id="tabOperaciones" layoutY="325" AnchorPane.leftAnchor="24" AnchorPane.rightAnchor="24" AnchorPane.bottomAnchor="135" tabClosingPolicy="UNAVAILABLE">
            <tabs>
                <Tab text="Historial">
                    <content>
                        <AnchorPane prefWidth="1000" prefHeight="220">
                            <children>
                                <TableView fx:id="tblTransacciones" AnchorPane.topAnchor="12" AnchorPane.leftAnchor="0" AnchorPane.rightAnchor="0" AnchorPane.bottomAnchor="58">
                                    <placeholder><Label text="Sin movimientos"/></placeholder>
            <columns>
                                        <TableColumn fx:id="tbcCodigo" text="Código" prefWidth="70"/>
                                        <TableColumn fx:id="tbcFecha" text="Fecha" prefWidth="120"/>
                                        <TableColumn fx:id="tbcTipo" text="Tipo" prefWidth="220"/>
                                        <TableColumn fx:id="tbcMonto" text="Monto" prefWidth="150"/>
                                        <TableColumn fx:id="tbcComision" text="Comisión" prefWidth="120"/>
                                        <TableColumn fx:id="tbcEstado" text="Estado" prefWidth="180"/>
                                    </columns>
                                </TableView>
                                <TextField fx:id="txtCodigo" promptText="Código de transacción" prefWidth="200" prefHeight="32" AnchorPane.leftAnchor="0" AnchorPane.bottomAnchor="12"/>
                                <Button text="Revertir transacción" onAction="#revertir" prefHeight="32" prefWidth="180" AnchorPane.leftAnchor="220" AnchorPane.bottomAnchor="12"/>
                                <Button text="Ver historial" onAction="#historial" prefHeight="32" prefWidth="150" AnchorPane.leftAnchor="420" AnchorPane.bottomAnchor="12"/>
                            </children>
                        </AnchorPane>
                    </content>
                </Tab>
                <Tab text="Puntos y beneficios">
                    <content>
                        <AnchorPane prefWidth="1000" prefHeight="220">
                            <children>
                                <Label text="Canje de puntos" layoutX="20" layoutY="15" textFill="#334155">
                                    <font>
                                        <Font name="System Bold" size="16"/>
                                    </font>
                                </Label>
                                <Button text="100 puntos: descuento" onAction="#canjear100" layoutX="20" layoutY="55" prefWidth="240" prefHeight="34"/>
                                <Button text="500 puntos: retiros gratis" onAction="#canjear500" layoutX="290" layoutY="55" prefWidth="260" prefHeight="34"/>
                                <Button text="1000 puntos: bono $50" onAction="#canjear1000" layoutX="580" layoutY="55" prefWidth="240" prefHeight="34"/>
                                <Button text="Historial de puntos" onAction="#historialPuntos" layoutX="20" layoutY="115" prefWidth="240" prefHeight="34"/>
                            </children>
                        </AnchorPane>
                    </content>
                </Tab>
                <Tab text="Programadas">
                    <content>
                        <AnchorPane prefWidth="1000" prefHeight="220">
                            <children>
                                <Label text="Monto y destino: campos superiores." layoutX="20" layoutY="10" textFill="#334155">
                                    <font>
                                        <Font name="System" size="13"/>
                                    </font>
                                </Label>
                                <ComboBox fx:id="cmbTipoProgramada" layoutX="20" layoutY="40" prefWidth="180" prefHeight="34" promptText=""/>
                                <DatePicker fx:id="dpFecha" layoutX="220" layoutY="40" prefWidth="180" prefHeight="34"/>
                                <ComboBox fx:id="cmbFrecuencia" layoutX="420" layoutY="40" prefWidth="150" prefHeight="34" promptText=""/>
                                <Button text="Programar" onAction="#programar" layoutX="590" layoutY="40" prefWidth="150" prefHeight="34"/>
                                <Button text="Ejecutar vencidas" onAction="#ejecutarProgramadas" layoutX="760" layoutY="40" prefWidth="190" prefHeight="34"/>
                                <Button text="Ver programadas" onAction="#verProgramadas" layoutX="20" layoutY="100" prefWidth="180" prefHeight="34"/>
                                <TextField fx:id="txtCodigoProgramada" layoutX="220" layoutY="100" prefWidth="190" prefHeight="34" promptText="Código de programada"/>
                                <Button text="Cancelar programada" onAction="#cancelarProgramada" layoutX="430" layoutY="100" prefWidth="230" prefHeight="34"/>
                            </children>
                        </AnchorPane>
                    </content>
                </Tab>
                <Tab text="Reportes">
                    <content>
                        <AnchorPane prefWidth="1000" prefHeight="220">
                            <children>
                                <Label text="Consultas" layoutX="20" layoutY="15" textFill="#334155">
                                    <font>
                                        <Font name="System Bold" size="16"/>
                                    </font>
                                </Label>
                                <Button text="Reporte de gastos" onAction="#reporte" layoutX="20" layoutY="55" prefWidth="220" prefHeight="34"/>
                                <Button text="Ver notificaciones" onAction="#verNotificaciones" layoutX="270" layoutY="55" prefWidth="220" prefHeight="34"/>
                            </children>
                        </AnchorPane>
                    </content>
                </Tab>
            </tabs>
        </TabPane>
        <TextArea fx:id="txtSalida" editable="false" wrapText="true" promptText="Resultado de la operación" prefHeight="105" AnchorPane.leftAnchor="24" AnchorPane.rightAnchor="24" AnchorPane.bottomAnchor="12"/>
    </children>
</AnchorPane>
```
