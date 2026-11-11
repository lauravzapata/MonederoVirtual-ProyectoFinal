package co.edu.uniquindio.poo.model;
import co.edu.uniquindio.poo.exceptions.DatoInvalidoException;
import co.edu.uniquindio.poo.exceptions.SaldoInsuficienteException;
public class Transferencia extends Transaccion {
    private Cliente destinatario;
    private Monedero origen;
    private Monedero destino;
    public Transferencia(int codigo, Cliente cliente, Monedero origen, Cliente destinatario,
                          Monedero destino, double monto, double comision, boolean sospechosa) {
        super(codigo, cliente, monto, comision, sospechosa);
        this.origen = origen; this.destinatario = destinatario; this.destino = destino;
    }
    @Override public String getTipo() { return "Transferencia"; }
    public Cliente getDestinatario() { return destinatario; }
    @Override public int calcularPuntos() { return (int)(getMonto() / 100) * 3; }
    @Override public void revertir() throws DatoInvalidoException, SaldoInsuficienteException {
        verificarReversion(); destino.retirar(getMonto()); origen.depositar(getMonto() + getComision()); marcarRevertida();
    }
    @Override public String toString() { return super.toString() + " | Para: " + destinatario.getNombre(); }
}
