package co.edu.uniquindio.poo.monedero.controller;

import co.edu.uniquindio.poo.monedero.model.Cliente;
import co.edu.uniquindio.poo.monedero.model.MonederoVirtual;
import co.edu.uniquindio.poo.monedero.exceptions.CuentaNoEncontradaException;
import co.edu.uniquindio.poo.monedero.exceptions.DatoInvalidoException;
import java.util.List;

public class ClienteController {
    private final MonederoVirtual monederoVirtual;

    public ClienteController(MonederoVirtual monederoVirtual) {
        this.monederoVirtual = monederoVirtual;
    }

    public Cliente registrarCliente(String documento, String nombre) throws DatoInvalidoException {
        return monederoVirtual.registrarCliente(documento, nombre);
    }

    public void editarNombre(Cliente cliente, String nombre) throws DatoInvalidoException {
        monederoVirtual.editarNombre(cliente, nombre);
    }

    public void eliminarCliente(String documento) throws CuentaNoEncontradaException, DatoInvalidoException {
        monederoVirtual.eliminarCliente(documento);
    }

    public List<Cliente> obtenerClientes() {
        return monederoVirtual.getListaClientes();
    }
}
