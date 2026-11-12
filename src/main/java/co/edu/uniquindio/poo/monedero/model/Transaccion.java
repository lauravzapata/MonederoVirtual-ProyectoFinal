package co.edu.uniquindio.poo.monedero.model;

import co.edu.uniquindio.poo.monedero.exceptions.DatoInvalidoException;
import co.edu.uniquindio.poo.monedero.exceptions.SaldoInsuficienteException;
import java.time.LocalDateTime;
public abstract class Transaccion implements IOperacion {
    private int codigo;
    private double monto;
    private double comision;
    private LocalDateTime fecha;
    private boolean revertida;
    private boolean sospechosa;
    private Cliente cliente;
    public Transaccion(int codigo, Cliente cliente, double monto, double comision, boolean sospechosa) {
        this.codigo = codigo;
        this.cliente = cliente;
        this.monto = monto;
        this.comision = comision;
        this.sospechosa = sospechosa;
        fecha = LocalDateTime.now();
    }

    public int getCodigo() {
        return codigo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    @Override
    public double getMonto() { return monto; }
    public double getComision() {
        return comision;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public boolean isRevertida() {
        return revertida;
    }

    public boolean isSospechosa() {
        return sospechosa;
    }

    protected void marcarRevertida() {
        revertida = true;
    }

    protected void verificarReversion() throws DatoInvalidoException {
        if (revertida) throw new DatoInvalidoException("Esa transacción ya fue revertida.");
    }

    public abstract void revertir() throws DatoInvalidoException, SaldoInsuficienteException;
    @Override
    public String toString() {
        return "#" + codigo + " | " + fecha.toLocalDate() + " | " + getTipo() + " | $" +
            String.format("%.2f", monto) + " | Comisión: $" + String.format("%.2f", comision) +
            (isRevertida() ? " | REVERTIDA" : "") + (sospechosa ? " | ALERTA ATÍPICA" : "");
    }
}
