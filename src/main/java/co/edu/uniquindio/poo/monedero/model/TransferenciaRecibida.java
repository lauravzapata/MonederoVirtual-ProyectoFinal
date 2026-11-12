package co.edu.uniquindio.poo.monedero.model;

import co.edu.uniquindio.poo.monedero.exceptions.DatoInvalidoException;

/** Refleja la misma transferencia en el historial del receptor, sin duplicar puntos. */
public class TransferenciaRecibida extends Transaccion {
    private final Transaccion original;
    public TransferenciaRecibida(Transaccion original) {
        super(original.getCodigo(), ((Transferencia) original).getDestinatario(),
                original.getMonto(), 0, original.isSospechosa());
        this.original = original;
    }

    @Override
    public String getTipo() { return "Transferencia recibida"; }
    @Override
    public int calcularPuntos() { return 0; }
    @Override
    public boolean isRevertida() { return original.isRevertida(); }
    @Override
    public void revertir() throws DatoInvalidoException {
        throw new DatoInvalidoException("La transferencia debe revertirla el emisor.");
    }
}
