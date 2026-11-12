package co.edu.uniquindio.poo.monedero.model;
import co.edu.uniquindio.poo.monedero.model.*;
import co.edu.uniquindio.poo.monedero.exceptions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
public class MonederoVirtualTest {
    @Test void depositoRetiroYSaldo() throws Exception {
        MonederoVirtual control = new MonederoVirtual("Monedero de prueba");
        Cliente ana = control.registrarCliente("1", "Ana");
        Monedero monedero = ana.getMonederos().get(0);
        control.depositar(ana, monedero, 1000);
        assertEquals(1000, monedero.getSaldo(), 0.001);
        assertEquals(10, ana.getPuntos());
        control.retirar(ana, monedero, 100);
        assertEquals(899, monedero.getSaldo(), 0.001);
        assertEquals(12, ana.getPuntos());
    }
    @Test void noPermiteRetiroSinSaldo() throws Exception {
        MonederoVirtual c = new MonederoVirtual("Monedero de prueba");
        Cliente ana = c.registrarCliente("1", "Ana");
        assertThrows(SaldoInsuficienteException.class, () -> c.retirar(ana, ana.getMonederos().get(0), 100));
    }
    @Test void transferenciaYReversion() throws Exception {
        MonederoVirtual c = new MonederoVirtual("Monedero de prueba");
        Cliente ana = c.registrarCliente("1", "Ana"); Cliente luis = c.registrarCliente("2", "Luis");
        Monedero a = ana.getMonederos().get(0), b = luis.getMonederos().get(0);
        c.depositar(ana, a, 1000);
        Transaccion t = c.transferir(ana, a, luis, b, 200);
        assertEquals(798, a.getSaldo(), 0.001);
        assertEquals(200, b.getSaldo(), 0.001);
        c.revertir(ana, t.getCodigo());
        assertEquals(1000, a.getSaldo(), 0.001);
        assertEquals(0, b.getSaldo(), 0.001);
        assertEquals(10, ana.getPuntos());
    }
    @Test void noPermiteRevertirTransferenciaGastada() throws Exception {
        MonederoVirtual c = new MonederoVirtual("Monedero de prueba");
        Cliente a = c.registrarCliente("1", "Ana"), b = c.registrarCliente("2", "Luis");
        c.depositar(a, a.getMonederos().get(0), 1000);
        Transaccion t = c.transferir(a, a.getMonederos().get(0), b, b.getMonederos().get(0), 200);
        c.retirar(b, b.getMonederos().get(0), 100);
        assertThrows(SaldoInsuficienteException.class, () -> c.revertir(a, t.getCodigo()));
        assertFalse(t.isRevertida());
    }
    @Test void programadaUnicaNoSeRepite() throws Exception {
        MonederoVirtual c = new MonederoVirtual("Monedero de prueba");
        Cliente ana = c.registrarCliente("1", "Ana");
        c.programar("Depósito", ana, ana.getMonederos().get(0), null, null, 500, LocalDate.now(), "Única");
        c.ejecutarPendientes(LocalDate.now()); c.ejecutarPendientes(LocalDate.now());
        assertEquals(500, ana.getMonederos().get(0).getSaldo(), 0.001);
    }
    @Test void rechazaClienteDuplicado() throws Exception {
        MonederoVirtual c = new MonederoVirtual("Monedero de prueba");
        c.registrarCliente("123", "Ana");
        assertThrows(DatoInvalidoException.class, () -> c.registrarCliente("123", "Otra"));
    }
}
