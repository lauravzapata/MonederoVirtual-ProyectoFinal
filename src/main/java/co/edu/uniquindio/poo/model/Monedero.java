package co.edu.uniquindio.poo.model;
import co.edu.uniquindio.poo.exceptions.DatoInvalidoException;
import co.edu.uniquindio.poo.exceptions.SaldoInsuficienteException;
public class Monedero {
    private String nombre;
    private double saldo;
    public Monedero(String nombre) { this.nombre = nombre; }
    public String getNombre() { return nombre; }
    public double getSaldo() { return saldo; }
    public void depositar(double monto) throws DatoInvalidoException {
        if (!Double.isFinite(monto) || monto <= 0) throw new DatoInvalidoException("El monto debe ser positivo.");
        monto = Math.round(monto * 100.0) / 100.0;
        if (monto == 0) throw new DatoInvalidoException("El monto debe ser de al menos un centavo.");
        saldo = Math.round((saldo + monto) * 100.0) / 100.0;
    }
    public void retirar(double monto) throws DatoInvalidoException, SaldoInsuficienteException {
        if (!Double.isFinite(monto) || monto <= 0) throw new DatoInvalidoException("El monto debe ser positivo.");
        // Normalizar antes de comparar: 0.56 + 0.01 puede resultar en 0.5700000000000001.
        monto = Math.round(monto * 100.0) / 100.0;
        if (monto == 0) throw new DatoInvalidoException("El monto debe ser de al menos un centavo.");
        if (monto > saldo) throw new SaldoInsuficienteException("No hay saldo suficiente en " + nombre + ".");
        saldo = Math.round((saldo - monto) * 100.0) / 100.0;
    }
    @Override public String toString() { return nombre + " ($" + String.format("%.2f", saldo) + ")"; }
}
