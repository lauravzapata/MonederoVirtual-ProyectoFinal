package co.edu.uniquindio.poo.monedero.model;

import java.time.LocalDate;
public class MovimientoPuntos {
    private LocalDate fecha;
    private int cantidad;
    private String descripcion;
    public MovimientoPuntos(int cantidad, String descripcion) {
        this.fecha = LocalDate.now();
        this.cantidad = cantidad;
        this.descripcion = descripcion;
    }

    public int getCantidad() {
        return cantidad;
    }

    @Override
    public String toString() { return fecha + " | " + descripcion + " | " + cantidad + " puntos"; }
}
