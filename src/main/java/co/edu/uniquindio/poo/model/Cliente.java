package co.edu.uniquindio.poo.model;
import java.time.LocalDate;
import java.util.ArrayList;
public class Cliente {
    private String documento;
    private String nombre;
    private ArrayList<Monedero> monederos = new ArrayList<>();
    private ArrayList<Transaccion> historial = new ArrayList<>();
    private ArrayList<MovimientoPuntos> movimientosPuntos = new ArrayList<>();
    private boolean descuentoTransferencia;
    private LocalDate retirosGratisHasta;
    public Cliente(String documento, String nombre) {
        this.documento = documento; this.nombre = nombre;
        monederos.add(new Monedero("Principal"));
    }
    public String getDocumento() { return documento; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public ArrayList<Monedero> getMonederos() { return monederos; }
    public ArrayList<Transaccion> getHistorial() { return historial; }
    public ArrayList<MovimientoPuntos> getMovimientosPuntos() { return movimientosPuntos; }
    public int getPuntos() {
        int total = 0;
        for (MovimientoPuntos movimiento : movimientosPuntos) total += movimiento.getCantidad();
        return total;
    }
    public void agregarPuntos(int cantidad, String descripcion) {
        movimientosPuntos.add(new MovimientoPuntos(cantidad, descripcion));
    }
    public boolean tieneDescuentoTransferencia() { return descuentoTransferencia; }
    public void setDescuentoTransferencia(boolean descuento) { descuentoTransferencia = descuento; }
    public boolean tieneRetirosGratis(LocalDate fecha) {
        return retirosGratisHasta != null && !fecha.isAfter(retirosGratisHasta);
    }
    public void activarRetirosGratis(LocalDate hasta) { retirosGratisHasta = hasta; }
    public String getRango() {
        if (getPuntos() <= 500) return "Bronce";
        if (getPuntos() <= 1000) return "Plata";
        if (getPuntos() <= 5000) return "Oro";
        return "Platino";
    }
    @Override public String toString() { return nombre + " (" + documento + ")"; }
}
