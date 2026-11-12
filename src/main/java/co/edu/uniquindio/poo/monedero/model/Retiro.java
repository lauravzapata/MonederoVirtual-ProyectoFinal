package co.edu.uniquindio.poo.monedero.model;

import co.edu.uniquindio.poo.monedero.exceptions.DatoInvalidoException;
public class Retiro extends Transaccion {
    private Monedero monedero;
    public Retiro(int codigo, Cliente cliente, Monedero monedero, double monto, double comision, boolean sospechosa) {
        super(codigo, cliente, monto, comision, sospechosa);
        this.monedero = monedero;
    }

    @Override
    public String getTipo() { return "Retiro"; }
    @Override
    public int calcularPuntos() { return (int)(getMonto() / 100) * 2; }
    @Override
    public void revertir() throws DatoInvalidoException {
        verificarReversion(); monedero.depositar(getMonto() + getComision()); marcarRevertida();
    }
}
