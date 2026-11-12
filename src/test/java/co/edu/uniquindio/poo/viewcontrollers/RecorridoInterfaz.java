package co.edu.uniquindio.poo.viewcontrollers;

import co.edu.uniquindio.poo.model.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

/** Recorrido ejecutable de los controles reales. Requiere JavaFX y un escritorio o Monocle. */
public class RecorridoInterfaz {
    private static FXMLLoader loader;
    private static Stage stage;
    private static Parent root;
    private static int comprobaciones;
    private static Throwable fallo;

    private static void comprobar(boolean condicion, String descripcion) {
        if (!condicion) throw new AssertionError(descripcion);
        comprobaciones++;
        System.out.println("OK: " + descripcion);
    }
    private static <T> T control(String id) { return (T) loader.getNamespace().get(id); }
    private static void texto(String id, String valor) { ((TextField) control(id)).setText(valor); }
    private static void elegir(String id, Object valor) { ((ComboBox<Object>) control(id)).setValue(valor); }
    private static String salida() { return ((TextArea) control("salida")).getText(); }
    private static void resultado(String fragmento) {
        comprobar(salida().contains(fragmento), "Mensaje: " + fragmento + " (recibido: " + salida().replace('\n',' ') + ")");
    }
    private static void pestana(String nombre) {
        TabPane tabs=(TabPane) root.lookup(".tab-pane");
        tabs.getSelectionModel().select(tabs.getTabs().stream().filter(t->t.getText().equals(nombre)).findFirst().orElseThrow());
        root.applyCss(); root.layout();
    }
    private static void pulsar(String nombre) {
        Button boton=root.lookupAll(".button").stream()
                .filter(n->n instanceof Button && ((Button)n).getText().equals(nombre))
                .map(n->(Button)n).findFirst().orElseThrow(()->new AssertionError("Botón no encontrado: "+nombre));
        boton.fire();
        root.applyCss(); root.layout();
    }
    private static Cliente registrar(String id, String nombre) {
        texto("documento",id);texto("nombre",nombre);pulsar("Registrar cliente");resultado("Cliente registrado");
        return ((ComboBox<Cliente>)control("cliente")).getValue();
    }
    private static void confirmarEliminacion() {
        Platform.runLater(()->{
            for(Window ventana:new ArrayList<>(Window.getWindows())) {
                if(ventana instanceof Stage && ventana!=stage && ventana.isShowing()) {
                    Node nodo=ventana.getScene().getRoot();
                    if(nodo instanceof DialogPane) ((Button)((DialogPane)nodo).lookupButton(ButtonType.OK)).fire();
                }
            }
        });
        pulsar("Eliminar cliente");
    }
    private static void recorrido() throws Exception {
        loader=new FXMLLoader(RecorridoInterfaz.class.getResource("/co/edu/uniquindio/poo/fxml/principal.fxml"));
        root=loader.load(); stage=new Stage();stage.setScene(new Scene(root,1120,840));stage.show();
        root.applyCss();root.layout();
        comprobar(root.lookup(".tab-pane")!=null,"FXML y tres pestañas cargan");
        texto("monto","100");pulsar("Depositar");resultado("cliente válido");
        pulsar("Ver historial");resultado("Selecciona un cliente");
        pestana("Puntos y beneficios");pulsar("100 puntos: descuento");resultado("cliente válido");
        pestana("Programadas");pulsar("Programar");resultado("cliente válido");
        pestana("Movimientos");
        Cliente ana=registrar("101","Ana"),luis=registrar("102","Luis");
        elegir("cliente",ana);
        texto("nombreMonedero","Ahorros");pulsar("Crear monedero");resultado("Monedero creado");
        Monedero ahorro=((ComboBox<Monedero>)control("monedero")).getValue();
        texto("monto","100");pulsar("Depositar");
        comprobar(ahorro.getSaldo()==100,"Depósito en Ahorros");
        comprobar(((ComboBox<?>)control("monedero")).getValue()==ahorro,"Conserva monedero tras operación");
        elegir("monedero",ana.getMonederos().get(0));texto("monto","1000");pulsar("Depositar");
        elegir("destinatario",luis);texto("monto","200");pulsar("Transferir");resultado("Transferencia realizada");
        comprobar(ana.getMonederos().get(0).getSaldo()==798,"Saldo del emisor con comisión");
        comprobar(luis.getMonederos().get(0).getSaldo()==200,"Saldo del receptor");
        int codigo=ana.getHistorial().get(ana.getHistorial().size()-1).getCodigo();
        elegir("cliente",luis);pulsar("Ver historial");resultado("Transferencia recibida");
        elegir("cliente",ana);texto("codigo",String.valueOf(codigo));pulsar("Revertir transacción");resultado("Transacción revertida");
        comprobar(ana.getMonederos().get(0).getSaldo()==1000 && luis.getMonederos().get(0).getSaldo()==0,"Reversión restaura ambos saldos");
        pulsar("Revertir transacción");resultado("ya fue revertida");
        texto("monto","abc");pulsar("Depositar");resultado("número válido");
        texto("monto","5000");pulsar("Retirar");resultado("saldo suficiente");
        texto("nombre","Ana María");pulsar("Editar nombre");resultado("Nombre actualizado");
        comprobar(ana.getNombre().equals("Ana María"),"Edición del cliente");
        texto("monto","100");pulsar("Retirar");resultado("Retiro realizado");
        pulsar("Reporte de gastos");resultado("Total de gastos");resultado("Comisiones");
        pestana("Puntos y beneficios");pulsar("1000 puntos: bono $50");resultado("suficientes puntos");
        pestana("Movimientos");texto("monto","200000");pulsar("Depositar");
        pestana("Puntos y beneficios");
        pulsar("100 puntos: descuento");resultado("Beneficio canjeado");
        pulsar("100 puntos: descuento");resultado("Ya tienes activo");
        pulsar("500 puntos: retiros gratis");resultado("Beneficio canjeado");
        double saldo=ana.getMonederos().get(0).getSaldo();
        pulsar("1000 puntos: bono $50");resultado("Beneficio canjeado");
        comprobar(ana.getMonederos().get(0).getSaldo()==saldo+50,"Bono suma 50");
        pulsar("Historial de puntos");resultado("Canje de beneficio");
        pestana("Movimientos");texto("monto","100");pulsar("Retirar");resultado("Comisión: $"+String.format("%.2f",0.0));
        texto("monto","50");
        pestana("Programadas");pulsar("Programar");resultado("Programación guardada");
        pulsar("Ver programadas");resultado("#1");
        pulsar("Ejecutar vencidas");resultado("realizadas: 1");
        pulsar("Ejecutar vencidas");resultado("realizadas: 0");
        ((DatePicker)control("fecha")).setValue(LocalDate.now().plusDays(1));
        elegir("tipoProgramada","Transferencia");elegir("frecuencia","Semanal");
        pulsar("Programar");resultado("Programación guardada");
        texto("codigoProgramada","2");pulsar("Cancelar programada");resultado("cancelada");
        pulsar("Cancelar programada");resultado("No existe");
        pulsar("Ver notificaciones");resultado("Transferencia recibida");resultado("Recordatorio");
        // Eliminar un destinatario seleccionado debe limpiar también su monedero.
        Cliente temporal=registrar("999","Temporal");
        elegir("destinatario",temporal);confirmarEliminacion();resultado("Cliente eliminado");
        comprobar(((ComboBox<?>)control("destinatario")).getValue()==null,"Limpia destinatario eliminado");
        comprobar(((ComboBox<?>)control("monederoDestino")).getValue()==null,"Limpia monedero del destinatario eliminado");
        comprobar(((ComboBox<?>)control("monedero")).getValue()==null,"Limpia monedero cuando no hay cliente");
        elegir("cliente",ana);confirmarEliminacion();resultado("No se elimina un cliente con historial");
        comprobar(((Label)control("resumen")).getText().contains("Saldo total"),"Resumen sigue disponible después del rechazo");
        stage.close();
    }
    public static void main(String[] args) throws Exception {
        CountDownLatch listo=new CountDownLatch(1);
        Platform.startup(()->{
            try{recorrido();}catch(Throwable e){fallo=e;e.printStackTrace();}
            finally {if(stage!=null)stage.close();listo.countDown();}
        });
        if(!listo.await(45,TimeUnit.SECONDS)) {System.err.println("Tiempo agotado en recorrido gráfico");System.exit(1);}
        Platform.exit();
        if(fallo!=null)System.exit(1);
        System.out.println("INTERFAZ: "+comprobaciones+" comprobaciones aprobadas");
    }
}
