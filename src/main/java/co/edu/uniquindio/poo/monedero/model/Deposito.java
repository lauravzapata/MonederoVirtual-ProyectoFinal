package co.edu.uniquindio.poo.monedero.model;

import co.edu.uniquindio.poo.monedero.exceptions.DatoInvalidoException;
import co.edu.uniquindio.poo.monedero.exceptions.SaldoInsuficienteException;
public class Deposito extends Transaccion {
    private Monedero monedero;
    public Deposito(int codigo, Cliente cliente, Monedero monedero, double monto, boolean sospechosa) {
        super(codigo, cliente, monto, 0, sospechosa);
        this.monedero = monedero;
    }

    @Override
    public String getTipo() { return "Depósito"; }
    @Override
    public int calcularPuntos() { return (int)(getMonto() / 100); }
    @Override
    public void revertir() throws DatoInvalidoException, SaldoInsuficienteException {
        verificarReversion(); monedero.retirar(getMonto()); marcarRevertida();
    }
}
