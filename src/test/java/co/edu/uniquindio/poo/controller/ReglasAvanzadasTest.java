package co.edu.uniquindio.poo.controller;

import co.edu.uniquindio.poo.model.*;
import co.edu.uniquindio.poo.exceptions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class ReglasAvanzadasTest {
    private final MonederoController c = new MonederoController();
    private Cliente ana() throws Exception { return c.registrarCliente("1", "Ana"); }

    @Test void montosInvalidosNoAlteranSaldoNiHistorial() throws Exception {
        Cliente a = ana(); Monedero m = a.getMonederos().get(0);
        for (double monto : new double[]{0, -1, Double.NaN, Double.POSITIVE_INFINITY, 1000000001})
            assertThrows(DatoInvalidoException.class, () -> c.depositar(a, m, monto));
        assertEquals(0, m.getSaldo()); assertTrue(a.getHistorial().isEmpty());
    }
    @Test void detectaAtipicaConHistorialPrevioYLimiteEstricto() throws Exception {
        Cliente a = ana(); Monedero m = a.getMonederos().get(0);
        c.depositar(a, m, 100); c.depositar(a, m, 100);
        assertFalse(c.depositar(a, m, 100).isSospechosa());
        assertTrue(c.depositar(a, m, 101).isSospechosa());
        assertFalse(c.getNotificaciones().isEmpty());
    }
    @Test void rangosEnSusFronteras() throws Exception {
        Cliente a = ana();
        int[] puntos = {0,500,501,1000,1001,5000,5001};
        String[] rangos = {"Bronce","Bronce","Plata","Plata","Oro","Oro","Platino"};
        for (int i=0;i<puntos.length;i++) {
            a.agregarPuntos(puntos[i]-a.getPuntos(), "Preparación");
            assertEquals(rangos[i],a.getRango());
        }
    }
    @Test void canjeNoPermiteDejarPuntosNegativosAlRevertir() throws Exception {
        Cliente a=ana(); Monedero m=a.getMonederos().get(0);
        Transaccion t=c.depositar(a,m,10000); c.canjear(a,m,100);
        assertEquals(0,a.getPuntos());
        assertThrows(DatoInvalidoException.class,()->c.revertir(a,t.getCodigo()));
        assertFalse(t.isRevertida()); assertEquals(10000,m.getSaldo());
        c.depositar(a,m,10000);
        assertThrows(DatoInvalidoException.class,()->c.canjear(a,m,100));
        assertEquals(100,a.getPuntos());
    }
    @Test void receptorVeTransferenciaYReversionSinGanarPuntos() throws Exception {
        Cliente a=ana(),b=c.registrarCliente("2","Luis");
        Monedero x=a.getMonederos().get(0),y=b.getMonederos().get(0);
        c.depositar(a,x,1000); Transaccion t=c.transferir(a,x,b,y,200);
        assertEquals(1,b.getHistorial().size()); assertEquals(0,b.getPuntos());
        assertThrows(DatoInvalidoException.class,()->c.revertir(b,t.getCodigo()));
        c.revertir(a,t.getCodigo()); assertTrue(b.getHistorial().get(0).isRevertida());
    }
    @Test void coleccionesNoSeModificanDesdeFuera() throws Exception {
        Cliente a=ana(); c.getClientes().clear(); a.getMonederos().clear();
        assertEquals(1,c.getClientes().size()); assertEquals(1,a.getMonederos().size());
    }
    @Test void procesaRecurrenciasAtrasadasUnaSolaVez() throws Exception {
        Cliente a=ana(); Monedero m=a.getMonederos().get(0); LocalDate hoy=LocalDate.now();
        c.programar("Depósito",a,m,null,null,100,hoy,"Semanal");
        c.ejecutarPendientes(hoy.plusWeeks(3));
        assertEquals(400,m.getSaldo()); assertEquals(4,c.getProgramadas().get(0).getEjecuciones());
        c.ejecutarPendientes(hoy.plusWeeks(3)); assertEquals(400,m.getSaldo());
    }
    @Test void falloProgramadoNoAvanzaFechaYPermiteReintento() throws Exception {
        Cliente a=ana(),b=c.registrarCliente("2","Luis"); Monedero m=a.getMonederos().get(0);
        TransaccionProgramada p=c.programar("Transferencia",a,m,b,b.getMonederos().get(0),100,LocalDate.now(),"Única");
        assertTrue(c.ejecutarPendientes(LocalDate.now()).contains("Fallidas: 1"));
        assertEquals(0,p.getEjecuciones()); c.depositar(a,m,200);
        c.ejecutarPendientes(LocalDate.now()); assertEquals(1,p.getEjecuciones());
    }
    @Test void rechazaOpcionesNulasYMonederoAjeno() throws Exception {
        Cliente a=ana(),b=c.registrarCliente("2","Luis");
        assertThrows(DatoInvalidoException.class,()->c.depositar(a,b.getMonederos().get(0),100));
        assertThrows(DatoInvalidoException.class,()->c.programar(null,a,a.getMonederos().get(0),null,null,100,LocalDate.now(),null));
    }
    @Test void reversionRepetidaNoCambiaDinero() throws Exception {
        Cliente a=ana(); Monedero m=a.getMonederos().get(0); Transaccion t=c.depositar(a,m,100);
        c.revertir(a,t.getCodigo());
        assertThrows(DatoInvalidoException.class,()->c.revertir(a,t.getCodigo()));
        assertEquals(0,m.getSaldo()); assertEquals(0,a.getPuntos());
    }
    @Test void beneficiosDeBonoYMesSinComisiones() throws Exception {
        Cliente a=ana(); Monedero m=a.getMonederos().get(0);
        c.depositar(a,m,200000); c.canjear(a,m,1000); assertEquals(200050,m.getSaldo());
        c.canjear(a,m,500); assertEquals(0,c.retirar(a,m,100).getComision());
    }

    @Test void comisionPorRangoYDescuentoCanjeado() throws Exception {
        Cliente a=ana(),b=c.registrarCliente("2","Luis"); Monedero m=a.getMonederos().get(0);
        c.depositar(a,m,200000); // Oro: 10% de reducción de comisión.
        c.canjear(a,m,100); // Descuento adicional del 10% sobre la comisión restante.
        Transaccion t=c.transferir(a,m,b,b.getMonederos().get(0),1000);
        assertEquals(8.10,t.getComision(),0.00001);
    }
    @Test void redondeoYCentavos() throws Exception {
        Cliente a=ana(); Monedero m=a.getMonederos().get(0);
        c.depositar(a,m,0.10); c.depositar(a,m,0.20);
        assertEquals(0.30,m.getSaldo(),0.000001);
        assertThrows(DatoInvalidoException.class,()->c.depositar(a,m,0.001));
    }
    @Test void ordenaRecurrenciasIntercaladasPorFecha() throws Exception {
        Cliente a=ana(); Monedero m=a.getMonederos().get(0);LocalDate hoy=LocalDate.now();
        c.programar("Depósito",a,m,null,null,100,hoy,"Semanal");
        c.programar("Depósito",a,m,null,null,200,hoy.plusDays(1),"Única");
        c.ejecutarPendientes(hoy.plusWeeks(1));
        assertEquals(100,a.getHistorial().get(0).getMonto());
        assertEquals(200,a.getHistorial().get(1).getMonto());
        assertEquals(100,a.getHistorial().get(2).getMonto());
    }
    @Test void noTransfiereAlMismoMonedero() throws Exception {
        Cliente a=ana();Monedero m=a.getMonederos().get(0);c.depositar(a,m,500);
        assertThrows(DatoInvalidoException.class,()->c.transferir(a,m,a,m,100));
        assertEquals(500,m.getSaldo());
    }
}
