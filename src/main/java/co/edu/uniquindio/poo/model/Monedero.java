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
        saldo += monto;
    }
    public void retirar(double monto) throws DatoInvalidoException, SaldoInsuficienteException {
        if (!Double.isFinite(monto) || monto <= 0) throw new DatoInvalidoException("El monto debe ser positivo.");
        if (monto > saldo) throw new SaldoInsuficienteException("No hay saldo suficiente en " + nombre + ".");
        saldo -= monto;
    }
    @Override public String toString() { return nombre + " ($" + String.format("%.2f", saldo) + ")"; }
}
