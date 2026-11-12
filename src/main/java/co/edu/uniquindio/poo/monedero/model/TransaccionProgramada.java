package co.edu.uniquindio.poo.monedero.model;

import java.time.LocalDate;
public class TransaccionProgramada {
    private int codigo;
    private String tipo;
    private Cliente origen;
    private Cliente destino;
    private Monedero monederoOrigen;
    private Monedero monederoDestino;
    private double monto;
    private LocalDate fecha;
    private String frecuencia;
    private int ejecuciones;
    public TransaccionProgramada(int codigo, String tipo, Cliente origen, Monedero desde,
            Cliente destino, Monedero hacia, double monto, LocalDate fecha, String frecuencia) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.origen = origen; monederoOrigen = desde;
        this.destino = destino; monederoDestino = hacia;
        this.monto = monto;
        this.fecha = fecha;
        this.frecuencia = frecuencia;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getTipo() {
        return tipo;
    }

    public Cliente getOrigen() {
        return origen;
    }

    public Monedero getMonederoOrigen() {
        return monederoOrigen;
    }

    public Cliente getDestino() {
        return destino;
    }

    public Monedero getMonederoDestino() {
        return monederoDestino;
    }

    public double getMonto() {
        return monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public int getEjecuciones() {
        return ejecuciones;
    }

    public void avanzar() {
        ejecuciones++;
        if (frecuencia.equals("Semanal")) fecha = fecha.plusWeeks(1);
        else if (frecuencia.equals("Mensual")) fecha = fecha.plusMonths(1);
        else fecha = null;
    }

    @Override
    public String toString() {
        return "#" + codigo + " | " + tipo + " | " + origen.getNombre() + " | $" + monto +
                " | Próxima: " + (fecha == null ? "Finalizada" : fecha) + " | " + frecuencia +
                " | Ejecutadas: " + ejecuciones;
    }
}
