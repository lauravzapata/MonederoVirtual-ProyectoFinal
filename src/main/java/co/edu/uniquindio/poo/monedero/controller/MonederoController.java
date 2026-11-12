package co.edu.uniquindio.poo.monedero.controller;

import co.edu.uniquindio.poo.monedero.model.Cliente;
import co.edu.uniquindio.poo.monedero.model.Monedero;
import co.edu.uniquindio.poo.monedero.model.MonederoVirtual;
import co.edu.uniquindio.poo.monedero.model.Transaccion;
import co.edu.uniquindio.poo.monedero.model.TransaccionProgramada;
import co.edu.uniquindio.poo.monedero.exceptions.CuentaNoEncontradaException;
import co.edu.uniquindio.poo.monedero.exceptions.DatoInvalidoException;
import co.edu.uniquindio.poo.monedero.exceptions.SaldoInsuficienteException;
import java.time.LocalDate;
import java.util.List;

public class MonederoController {
    private final MonederoVirtual monederoVirtual;

    public MonederoController(MonederoVirtual monederoVirtual) {
        this.monederoVirtual = monederoVirtual;
    }

    public String getNombre() {
        return monederoVirtual.getNombre();
    }

    public Monedero registrarMonedero(Cliente cliente, String nombre) throws DatoInvalidoException {
        return monederoVirtual.registrarMonedero(cliente, nombre);
    }

    public Transaccion depositar(Cliente cliente, Monedero monedero, double monto) throws DatoInvalidoException {
        return monederoVirtual.depositar(cliente, monedero, monto);
    }

    public Transaccion retirar(Cliente cliente, Monedero monedero, double monto)
            throws DatoInvalidoException, SaldoInsuficienteException {
        return monederoVirtual.retirar(cliente, monedero, monto);
    }

    public Transaccion transferir(Cliente cliente, Monedero desde, Cliente destinatario, Monedero hacia, double monto)
            throws DatoInvalidoException, SaldoInsuficienteException {
        return monederoVirtual.transferir(cliente, desde, destinatario, hacia, monto);
    }

    public void revertir(Cliente cliente, int codigo)
            throws DatoInvalidoException, SaldoInsuficienteException, CuentaNoEncontradaException {
        monederoVirtual.revertir(cliente, codigo);
    }

    public void canjear(Cliente cliente, Monedero monedero, int puntos) throws DatoInvalidoException {
        monederoVirtual.canjear(cliente, monedero, puntos);
    }

    public TransaccionProgramada programar(String tipo, Cliente cliente, Monedero desde, Cliente destino,
                                           Monedero hacia, double monto, LocalDate fecha, String frecuencia)
            throws DatoInvalidoException {
        return monederoVirtual.programar(tipo, cliente, desde, destino, hacia, monto, fecha, frecuencia);
    }

    public void cancelarProgramada(int codigo) throws CuentaNoEncontradaException {
        monederoVirtual.cancelarProgramada(codigo);
    }

    public String ejecutarPendientes(LocalDate hoy) {
        return monederoVirtual.ejecutarPendientes(hoy);
    }

    public String reporte(Cliente cliente) throws DatoInvalidoException {
        return monederoVirtual.reporte(cliente);
    }

    public List<Cliente> obtenerClientes() {
        return monederoVirtual.getListaClientes();
    }

    public List<TransaccionProgramada> obtenerProgramadas() {
        return monederoVirtual.getListaProgramadas();
    }

    public List<String> obtenerNotificaciones() {
        return monederoVirtual.getListaNotificaciones();
    }

}
