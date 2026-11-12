package co.edu.uniquindio.poo.controller;

import co.edu.uniquindio.poo.exceptions.*;
import co.edu.uniquindio.poo.model.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/** Casos límite y recorridos de negocio adicionales a las pruebas iniciales. */
class CasosLimiteTest {
    private final MonederoController c = new MonederoController();
    private Cliente cliente(String id) throws Exception { return c.registrarCliente(id, "Cliente " + id); }
    private Monedero principal(Cliente a) { return a.getMonederos().get(0); }

    @Test void retiroConSaldoExactoIncluidaComisionDeCentavos() throws Exception {
        Cliente a=cliente("1"); Monedero m=principal(a);
        c.depositar(a,m,0.57); c.retirar(a,m,0.56);
        assertEquals(0,m.getSaldo(),0.000001);
    }
    @Test void transferenciaConSaldoExactoIncluidaComisionDeCentavos() throws Exception {
        Cliente a=cliente("1"),b=cliente("2");
        c.depositar(a,principal(a),0.57);
        c.transferir(a,principal(a),b,principal(b),0.56);
        assertEquals(0,principal(a).getSaldo(),0.000001);
        assertEquals(0.56,principal(b).getSaldo(),0.000001);
    }
    @Test void montoMenorAUnCentavoNoRegistraOperacionesDeValorCero() throws Exception {
        Cliente a=cliente("1");
        assertThrows(DatoInvalidoException.class,()->c.depositar(a,principal(a),0.000000001));
        assertTrue(a.getHistorial().isEmpty());
    }
    @Test void documentosYMonederosSeValidanSinEspaciosNiDuplicados() throws Exception {
        Cliente a=c.registrarCliente(" 101 "," Ana ");
        assertEquals("101",a.getDocumento()); assertEquals("Ana",a.getNombre());
        assertThrows(DatoInvalidoException.class,()->c.registrarCliente("101","Otra"));
        c.registrarMonedero(a," Ahorros ");
        assertThrows(DatoInvalidoException.class,()->c.registrarMonedero(a,"ahorros"));
        assertThrows(DatoInvalidoException.class,()->c.registrarMonedero(a," "));
    }
    @Test void editarYEliminarClienteSinMovimientos() throws Exception {
        Cliente a=cliente("1"); c.editarNombre(a," Ana "); assertEquals("Ana",a.getNombre());
        assertThrows(DatoInvalidoException.class,()->c.editarNombre(a," "));
        c.eliminarCliente("1"); assertNull(c.buscarCliente("1"));
        assertThrows(DatoInvalidoException.class,()->c.depositar(a,principal(a),100));
        assertThrows(CuentaNoEncontradaException.class,()->c.eliminarCliente("1"));
    }
    @Test void noEliminaHistorialNiClienteConProgramadaPendiente() throws Exception {
        Cliente a=cliente("1"),b=cliente("2");
        Transaccion t=c.depositar(a,principal(a),100);c.revertir(a,t.getCodigo());
        assertThrows(DatoInvalidoException.class,()->c.eliminarCliente("1"));
        TransaccionProgramada p=c.programar("Depósito",b,principal(b),null,null,100,LocalDate.now(),"Única");
        assertThrows(DatoInvalidoException.class,()->c.eliminarCliente("2"));
        c.cancelarProgramada(p.getCodigo());c.eliminarCliente("2"); assertNull(c.buscarCliente("2"));
    }
    @Test void transferenciaSinFondosNoMueveDineroPuntosNiHistorial() throws Exception {
        Cliente a=cliente("1"),b=cliente("2");c.depositar(a,principal(a),100);
        assertThrows(SaldoInsuficienteException.class,()->c.transferir(a,principal(a),b,principal(b),100));
        assertEquals(100,principal(a).getSaldo());assertEquals(0,principal(b).getSaldo());
        assertEquals(1,a.getHistorial().size());assertTrue(b.getHistorial().isEmpty());assertEquals(1,a.getPuntos());
    }
    @Test void reversionDeRetiroDevuelveComisionYRestaPuntos() throws Exception {
        Cliente a=cliente("1");c.depositar(a,principal(a),1000);
        Transaccion t=c.retirar(a,principal(a),200);c.revertir(a,t.getCodigo());
        assertEquals(1000,principal(a).getSaldo());assertEquals(10,a.getPuntos());assertTrue(t.isRevertida());
    }
    @Test void depositoGastadoNoSeRevierteNiRestaPuntos() throws Exception {
        Cliente a=cliente("1");Transaccion d=c.depositar(a,principal(a),1000);c.retirar(a,principal(a),100);
        assertThrows(SaldoInsuficienteException.class,()->c.revertir(a,d.getCodigo()));
        assertEquals(899,principal(a).getSaldo());assertEquals(12,a.getPuntos());assertFalse(d.isRevertida());
    }
    @Test void canjeSinPuntosOInvalidoNoEntregaBeneficios() throws Exception {
        Cliente a=cliente("1");
        for(int puntos:new int[]{100,500,1000,200})
            assertThrows(DatoInvalidoException.class,()->c.canjear(a,principal(a),puntos));
        assertEquals(0,principal(a).getSaldo());assertEquals(0,a.getPuntos());
        assertFalse(a.tieneDescuentoTransferencia());assertFalse(a.tieneRetirosGratis(LocalDate.now()));
    }
    @Test void extiendeMesGratisYRespetaVencimiento() throws Exception {
        Cliente a=cliente("1");c.depositar(a,principal(a),200000);
        LocalDate hoy=LocalDate.now();c.canjear(a,principal(a),500);c.canjear(a,principal(a),500);
        assertTrue(a.tieneRetirosGratis(hoy.plusMonths(1).plusMonths(1)));
        assertFalse(a.tieneRetirosGratis(hoy.plusMonths(1).plusMonths(1).plusDays(1)));
        a.activarRetirosGratis(hoy.minusDays(1));assertEquals(1,c.retirar(a,principal(a),100).getComision());
    }
    @Test void cuentasSeparanSaldoYCompartenPuntosDelCliente() throws Exception {
        Cliente a=cliente("1");Monedero ahorro=c.registrarMonedero(a,"Ahorros");
        c.depositar(a,principal(a),100);c.depositar(a,ahorro,200);
        assertEquals(100,principal(a).getSaldo());assertEquals(200,ahorro.getSaldo());assertEquals(3,a.getPuntos());
    }
    @Test void detectorUsaDesviacionYExcluyeReversiones() throws Exception {
        Cliente a=cliente("1");c.depositar(a,principal(a),100);c.depositar(a,principal(a),200);
        // Media 150, desviación 50: límite 250. No basta con superar la media.
        assertFalse(c.depositar(a,principal(a),240).isSospechosa());
        Transaccion grande=c.depositar(a,principal(a),10000);assertTrue(grande.isSospechosa());
        c.revertir(a,grande.getCodigo());
        assertTrue(c.depositar(a,principal(a),1000).isSospechosa());
    }
    @Test void saldoBajoYCanjesSeRegistranEnSusHistoriales() throws Exception {
        Cliente a=cliente("1");c.depositar(a,principal(a),200);c.retirar(a,principal(a),100);
        assertTrue(c.getNotificaciones().stream().anyMatch(n->n.contains("Saldo bajo")));
        c.depositar(a,principal(a),10000);c.canjear(a,principal(a),100);
        assertTrue(a.getMovimientosPuntos().stream().anyMatch(m->m.getCantidad()==-100));
    }
    @Test void reporteIncluyeComisionesYExcluyeRevertidas() throws Exception {
        Cliente a=cliente("1"),b=cliente("2");c.depositar(a,principal(a),1000);
        Transaccion t=c.transferir(a,principal(a),b,principal(b),200);c.retirar(a,principal(a),100);
        String reporte=c.reporte(a);
        assertTrue(reporte.contains("Total de gastos: $"+String.format("%.2f",303.0)));
        assertTrue(reporte.contains("Gasto promedio por operación: $"+String.format("%.2f",151.5)));
        c.revertir(a,t.getCodigo());
        assertTrue(c.reporte(a).contains("Total de gastos: $"+String.format("%.2f",101.0)));
        assertTrue(c.reporte(b).contains("Transferencias recibidas: $"+String.format("%.2f",0.0)));
    }
    @Test void cancelarYProgramarParaElFuturoNoEjecutaAntes() throws Exception {
        Cliente a=cliente("1");LocalDate hoy=LocalDate.now();
        TransaccionProgramada p=c.programar("Depósito",a,principal(a),null,null,100,hoy.plusDays(1),"Única");
        c.ejecutarPendientes(hoy);assertEquals(0,principal(a).getSaldo());
        c.cancelarProgramada(p.getCodigo());c.ejecutarPendientes(hoy.plusDays(1));assertEquals(0,principal(a).getSaldo());
        assertThrows(CuentaNoEncontradaException.class,()->c.cancelarProgramada(p.getCodigo()));
    }
    @Test void validacionesDeProgramacionNoGuardanDatosInvalidos() throws Exception {
        Cliente a=cliente("1");LocalDate hoy=LocalDate.now();
        for(LocalDate fecha:new LocalDate[]{null,hoy.minusDays(1)})
            assertThrows(DatoInvalidoException.class,()->c.programar("Depósito",a,principal(a),null,null,100,fecha,"Única"));
        assertThrows(DatoInvalidoException.class,()->c.programar("Depósito",a,principal(a),null,null,100,hoy,"Diaria"));
        assertThrows(DatoInvalidoException.class,()->c.programar("Transferencia",a,principal(a),null,null,100,hoy,"Única"));
        assertTrue(c.getProgramadas().isEmpty());
    }
    @Test void mensualEnFinDeMesAvanzaAFechaValida() {
        Cliente a=new Cliente("1","Ana");
        TransaccionProgramada p=new TransaccionProgramada(1,"Depósito",a,principal(a),null,null,100,LocalDate.of(2028,1,31),"Mensual");
        p.avanzar();assertEquals(LocalDate.of(2028,2,29),p.getFecha());
        p.avanzar();assertEquals(LocalDate.of(2028,3,29),p.getFecha());assertEquals(2,p.getEjecuciones());
    }
    @Test void falloDeUnaProgramadaNoBloqueaLasDemas() throws Exception {
        Cliente a=cliente("1"),b=cliente("2");LocalDate hoy=LocalDate.now();
        c.programar("Transferencia",a,principal(a),b,principal(b),100,hoy,"Única");
        c.programar("Depósito",b,principal(b),null,null,200,hoy,"Única");
        String resultado=c.ejecutarPendientes(hoy);
        assertEquals("Operaciones realizadas: 1 | Fallidas: 1",resultado);assertEquals(200,principal(b).getSaldo());
    }
}
