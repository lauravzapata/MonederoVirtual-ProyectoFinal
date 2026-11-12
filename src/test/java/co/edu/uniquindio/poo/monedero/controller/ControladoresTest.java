package co.edu.uniquindio.poo.monedero.controller;

import co.edu.uniquindio.poo.monedero.model.Cliente;
import co.edu.uniquindio.poo.monedero.model.MonederoVirtual;
import co.edu.uniquindio.poo.monedero.model.Transaccion;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ControladoresTest {
    @Test
    void clientesYOperacionesCompartenElModeloAlCrearOtroControlador() throws Exception {
        MonederoVirtual modelo = new MonederoVirtual("Monedero de prueba");
        ClienteController clientes = new ClienteController(modelo);
        MonederoController operaciones = new MonederoController(modelo);
        Cliente ana = clientes.registrarCliente("101", "Ana");
        Cliente luis = clientes.registrarCliente("102", "Luis");
        operaciones.depositar(ana, ana.getMonederos().get(0), 1000);
        Transaccion envio = operaciones.transferir(ana, ana.getMonederos().get(0),
                luis, luis.getMonederos().get(0), 200);
        MonederoController otraPantalla = new MonederoController(modelo);
        assertSame(ana, otraPantalla.obtenerClientes().get(0));
        assertEquals(798, ana.getMonederos().get(0).getSaldo());
        otraPantalla.revertir(ana, envio.getCodigo());
        assertEquals(1000, ana.getMonederos().get(0).getSaldo());
        assertEquals(0, luis.getMonederos().get(0).getSaldo());
        assertEquals(10, ana.getPuntos());
    }
}
