package co.edu.uniquindio.poo.controller;

import co.edu.uniquindio.poo.model.*;
import co.edu.uniquindio.poo.exceptions.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

/** Contiene las reglas del negocio. Los datos se conservan durante la ejecución. */
public class MonederoController {
    private ArrayList<Cliente> clientes = new ArrayList<>();
    private ArrayList<TransaccionProgramada> programadas = new ArrayList<>();
    private ArrayList<String> notificaciones = new ArrayList<>();
    private int siguienteCodigo = 1;
    private int siguienteProgramada = 1;

    public ArrayList<Cliente> getClientes() { return clientes; }
    public ArrayList<TransaccionProgramada> getProgramadas() { return programadas; }
    public ArrayList<String> getNotificaciones() { return notificaciones; }

    public Cliente registrarCliente(String documento, String nombre) throws DatoInvalidoException {
        if (documento == null || documento.trim().isEmpty() || nombre == null || nombre.trim().isEmpty())
            throw new DatoInvalidoException("Ingresa el documento y el nombre.");
        if (buscarCliente(documento.trim()) != null) throw new DatoInvalidoException("El cliente ya existe.");
        Cliente nuevo = new Cliente(documento.trim(), nombre.trim());
        clientes.add(nuevo);
        return nuevo;
    }
    public Cliente buscarCliente(String documento) {
        // Stream y lambda: el profesor los exige en el proyecto final.
        return clientes.stream().filter(c -> c.getDocumento().equals(documento)).findFirst().orElse(null);
    }
    public void eliminarCliente(String documento) throws CuentaNoEncontradaException, DatoInvalidoException {
        Cliente cliente = buscarCliente(documento);
        if (cliente == null) throw new CuentaNoEncontradaException("No se encontró el cliente.");
        for (Monedero m : cliente.getMonederos())
            if (m.getSaldo() > 0) throw new DatoInvalidoException("Primero retira o transfiere todos los saldos.");
        for (TransaccionProgramada p : programadas)
            if (p.getOrigen() == cliente || p.getDestino() == cliente)
                throw new DatoInvalidoException("Primero cancela las operaciones programadas de este cliente.");
        clientes.remove(cliente);
    }
    public void editarNombre(Cliente cliente, String nombre) throws DatoInvalidoException {
        verificarCliente(cliente);
        if (nombre == null || nombre.trim().isEmpty()) throw new DatoInvalidoException("El nombre está vacío.");
        // El nombre se cambia con un método de la clase Cliente.
        cliente.setNombre(nombre.trim());
    }
    public Monedero registrarMonedero(Cliente cliente, String nombre) throws DatoInvalidoException {
        verificarCliente(cliente);
        if (nombre == null || nombre.trim().isEmpty()) throw new DatoInvalidoException("Ingresa un nombre de monedero.");
        for (Monedero m : cliente.getMonederos())
            if (m.getNombre().equalsIgnoreCase(nombre.trim())) throw new DatoInvalidoException("Ya existe ese monedero.");
        Monedero nuevo = new Monedero(nombre.trim());
        cliente.getMonederos().add(nuevo);
        return nuevo;
    }
    private void verificarCliente(Cliente cliente) throws DatoInvalidoException {
        if (cliente == null || !clientes.contains(cliente)) throw new DatoInvalidoException("Selecciona un cliente válido.");
    }
    private void verificarMonedero(Cliente cliente, Monedero monedero) throws DatoInvalidoException {
        verificarCliente(cliente);
        if (monedero == null || !cliente.getMonederos().contains(monedero))
            throw new DatoInvalidoException("Selecciona un monedero de ese cliente.");
    }
    private void verificarMonto(double monto) throws DatoInvalidoException {
        if (!Double.isFinite(monto) || monto <= 0 || monto > 1000000000)
            throw new DatoInvalidoException("Ingresa un monto mayor a cero y hasta mil millones.");
    }
    private boolean esAtipica(Cliente cliente, double monto) {
        double suma = 0;
        int cantidad = 0;
        for (Transaccion t : cliente.getHistorial()) {
            if (!t.isRevertida()) { suma += t.getMonto(); cantidad++; }
        }
        if (cantidad < 2) return false;
        double promedio = suma / cantidad;
        double sumaCuadrados = 0;
        for (Transaccion t : cliente.getHistorial()) {
            if (!t.isRevertida())
                sumaCuadrados += (t.getMonto() - promedio) * (t.getMonto() - promedio);
        }
        double desviacion = Math.sqrt(sumaCuadrados / cantidad);
        return monto > promedio + 2 * desviacion;
    }
    private void registrar(Transaccion transaccion) {
        Cliente cliente = transaccion.getCliente();
        cliente.getHistorial().add(transaccion);
        cliente.agregarPuntos(transaccion.calcularPuntos(), "Transacción #" + transaccion.getCodigo());
        if (transaccion.isSospechosa()) {
            notificaciones.add("ALERTA para " + cliente.getNombre() + ": operación atípica #" + transaccion.getCodigo());
        }
    }
    public Transaccion depositar(Cliente cliente, Monedero monedero, double monto) throws DatoInvalidoException {
        verificarMonedero(cliente, monedero); verificarMonto(monto);
        boolean alerta = esAtipica(cliente, monto);
        monedero.depositar(monto);
        Transaccion transaccion = new Deposito(siguienteCodigo++, cliente, monedero, monto, alerta);
        registrar(transaccion);
        return transaccion;
    }
    public Transaccion retirar(Cliente cliente, Monedero monedero, double monto)
            throws DatoInvalidoException, SaldoInsuficienteException {
        verificarMonedero(cliente, monedero); verificarMonto(monto);
        double comision = cliente.tieneRetirosGratis(LocalDate.now()) ? 0 : monto * 0.01;
        boolean alerta = esAtipica(cliente, monto);
        monedero.retirar(monto + comision);
        Transaccion transaccion = new Retiro(siguienteCodigo++, cliente, monedero, monto, comision, alerta);
        registrar(transaccion);
        avisarSaldoBajo(cliente, monedero);
        return transaccion;
    }
    public Transaccion transferir(Cliente cliente, Monedero desde, Cliente destinatario, Monedero hacia, double monto)
            throws DatoInvalidoException, SaldoInsuficienteException {
        verificarMonedero(cliente, desde); verificarMonedero(destinatario, hacia); verificarMonto(monto);
        if (desde == hacia) throw new DatoInvalidoException("Elige otro monedero de destino.");
        double comision = monto * 0.01;
        if (cliente.tieneDescuentoTransferencia()) comision = comision * 0.90;
        boolean alerta = esAtipica(cliente, monto);
        desde.retirar(monto + comision);
        hacia.depositar(monto);
        Transaccion transaccion = new Transferencia(siguienteCodigo++, cliente, desde,
                destinatario, hacia, monto, comision, alerta);
        registrar(transaccion);
        avisarSaldoBajo(cliente, desde);
        return transaccion;
    }
    private void avisarSaldoBajo(Cliente cliente, Monedero monedero) {
        if (monedero.getSaldo() < 100)
            notificaciones.add("Saldo bajo: " + cliente.getNombre() + " / " + monedero.getNombre());
    }
    public void revertir(Cliente cliente, int codigo)
            throws DatoInvalidoException, SaldoInsuficienteException, CuentaNoEncontradaException {
        verificarCliente(cliente);
        for (Transaccion t : cliente.getHistorial()) {
            if (t.getCodigo() == codigo) {
                t.revertir();
                cliente.agregarPuntos(-t.calcularPuntos(), "Reversión #" + codigo);
                return;
            }
        }
        throw new CuentaNoEncontradaException("No existe esa transacción en el historial del cliente.");
    }
    public void canjear(Cliente cliente, Monedero monedero, int puntos) throws DatoInvalidoException {
        verificarMonedero(cliente, monedero);
        if (puntos != 100 && puntos != 500 && puntos != 1000)
            throw new DatoInvalidoException("Elige un beneficio válido.");
        if (cliente.getPuntos() < puntos) throw new DatoInvalidoException("No tienes suficientes puntos.");
        if (puntos == 100) cliente.setDescuentoTransferencia(true);
        if (puntos == 500) cliente.activarRetirosGratis(LocalDate.now().plusMonths(1));
        if (puntos == 1000) monedero.depositar(50);
        cliente.agregarPuntos(-puntos, "Canje de beneficio");
    }
    public TransaccionProgramada programar(String tipo, Cliente cliente, Monedero desde, Cliente destino,
                                           Monedero hacia, double monto, LocalDate fecha, String frecuencia)
            throws DatoInvalidoException {
        verificarMonedero(cliente, desde); verificarMonto(monto);
        if (!tipo.equals("Depósito") && !tipo.equals("Transferencia"))
            throw new DatoInvalidoException("Elige depósito o transferencia.");
        if (tipo.equals("Transferencia")) {
            verificarMonedero(destino, hacia);
            if (desde == hacia) throw new DatoInvalidoException("El origen y el destino son iguales.");
        }
        if (fecha == null || fecha.isBefore(LocalDate.now()))
            throw new DatoInvalidoException("Elige una fecha actual o futura.");
        if (!frecuencia.equals("Única") && !frecuencia.equals("Semanal") && !frecuencia.equals("Mensual"))
            throw new DatoInvalidoException("Frecuencia inválida.");
        TransaccionProgramada p = new TransaccionProgramada(siguienteProgramada++, tipo,
                cliente, desde, destino, hacia, monto, fecha, frecuencia);
        programadas.add(p);
        ordenarProgramadas();
        notificaciones.add("Recordatorio: " + cliente.getNombre() + " programó una operación para " + fecha);
        return p;
    }
    public void cancelarProgramada(int codigo) throws CuentaNoEncontradaException {
        for (TransaccionProgramada p : programadas) {
            if (p.getCodigo() == codigo) { programadas.remove(p); return; }
        }
        throw new CuentaNoEncontradaException("No existe esa operación programada.");
    }
    public String ejecutarPendientes(LocalDate hoy) {
        int realizadas = 0;
        int fallidas = 0;
        ordenarProgramadas();
        // Una ejecución por cada operación vencida; las recurrentes avanzan a su siguiente fecha.
        for (TransaccionProgramada p : programadas) {
            if (p.getFecha() != null && !p.getFecha().isAfter(hoy)) {
                try {
                    if (p.getTipo().equals("Depósito")) depositar(p.getOrigen(), p.getMonederoOrigen(), p.getMonto());
                    else transferir(p.getOrigen(), p.getMonederoOrigen(), p.getDestino(), p.getMonederoDestino(), p.getMonto());
                    p.avanzar();
                    realizadas++;
                } catch (DatoInvalidoException | SaldoInsuficienteException e) {
                    fallidas++;
                    notificaciones.add("No se ejecutó la programada #" + p.getCodigo() + ": " + e.getMessage());
                }
            }
        }
        ordenarProgramadas();
        return "Operaciones realizadas: " + realizadas + " | Fallidas: " + fallidas;
    }
    private void ordenarProgramadas() {
        programadas.sort((a, b) -> {
            if (a.getFecha() == null && b.getFecha() == null) return 0;
            if (a.getFecha() == null) return 1;
            if (b.getFecha() == null) return -1;
            return a.getFecha().compareTo(b.getFecha());
        });
    }
    public String reporte(Cliente cliente) throws DatoInvalidoException {
        verificarCliente(cliente);
        // Stream API: filtros y sumas para reportes, como exige el enunciado.
        double depositos = cliente.getHistorial().stream()
                .filter(t -> !t.isRevertida() && t.getTipo().equals("Depósito"))
                .mapToDouble(t -> t.getMonto()).sum();
        double retiros = cliente.getHistorial().stream()
                .filter(t -> !t.isRevertida() && t.getTipo().equals("Retiro"))
                .mapToDouble(t -> t.getMonto()).sum();
        double transferencias = cliente.getHistorial().stream()
                .filter(t -> !t.isRevertida() && t.getTipo().equals("Transferencia"))
                .mapToDouble(t -> t.getMonto()).sum();
        long alertas = cliente.getHistorial().stream().filter(t -> t.isSospechosa() && !t.isRevertida()).count();
        return "REPORTE DE " + cliente.getNombre() + "\nDepósitos: $" + String.format("%.2f", depositos) +
                "\nRetiros: $" + String.format("%.2f", retiros) +
                "\nTransferencias: $" + String.format("%.2f", transferencias) +
                "\nTotal de gastos: $" + String.format("%.2f", retiros + transferencias) +
                "\nAlertas: " + alertas + "\nPuntos actuales: " + cliente.getPuntos() +
                "\nRango: " + cliente.getRango();
    }
}
