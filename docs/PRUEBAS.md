# Pruebas realizadas

Fecha: 12 de noviembre de 2026 (Colombia).

## Reglas del negocio

Se ejecutó `clean test` con JDK 21, Maven 3.8.5 y JavaFX 21.0.6.

**41 pruebas JUnit: 0 fallos, 0 errores y 0 omitidas.**

| Grupo | Pruebas | Contenido |
| --- | ---: | --- |
| MonederoVirtualTest | 6 | Depósitos, retiros, saldo, transferencias, reversiones, programación única y duplicados |
| ReglasAvanzadasTest | 15 | Puntos, rangos, canjes, comisiones, análisis de atípicas, validaciones y recurrencias |
| CasosLimiteTest | 19 | Centavos, saldo exacto, operaciones rechazadas sin cambios, multimonedero, reportes, fechas y reintentos |
| ControladoresTest | 1 | Modelo compartido por los controladores |

Para repetirlas: `mvnw.cmd test` en Windows o `./mvnw test` en Linux/macOS.

## Interfaz

Se inició la aplicación real y se cargaron Inicio, Gestión, Clientes y Monedero. La comprobación gráfica utilizó JavaFX para Linux con Monocle como herramienta externa al proyecto.

**66 comprobaciones aprobadas**, independientes de las 41 pruebas JUnit:

- Inicio abre la tabla de clientes y el acceso directo a depósitos.
- El botón Depositar dinero está visible y habilitado dentro de la ventana, también con el tamaño mínimo y al seleccionar beneficios, programadas y reportes.
- Registrar un cliente crea su monedero Principal; al cambiar a Monedero se cargan las selecciones.
- Depositar 1000 cambia el saldo a 1000, suma 10 puntos y agrega la fila del historial.
- Cambiar entre monederos muestra el saldo de cada uno, sin mezclarlo con el total del cliente.
- Los depósitos aceptan coma decimal. Se rechazan números inválidos, cero y retiros sin fondos.
- La tabla de clientes actualiza los puntos al volver a seleccionarla.
- Seleccionar una fila permite actualizar un cliente y completar el código de una reversión.
- Transferencias, reversiones, canjes, programación, reportes y notificaciones responden desde sus controles.
- Volver a Inicio conserva los datos del modelo compartido.

Los scripts de automatización gráfica quedan fuera de las fuentes de entrega. Las fuentes del proyecto mantienen los patrones de los archivos de referencia.

## Evidencia

- [Resultado JUnit](resultado-pruebas.txt).
- [Resultado de las comprobaciones gráficas](resultado-interfaz.txt).
- [Inicio](inicio.png), [Clientes](clientes.png) y [Monedero con un depósito de 1000](interfaz.png).

Las capturas corresponden a esta interfaz básica con tablas y pestañas. No se verificó la apariencia específica en Windows/macOS ni todos los estados posibles del programa.

## Alcance

Los datos están en memoria. Las alertas son internas y no se envían mensajes externos. Las programadas se procesan con el botón Ejecutar vencidas. El modelo usa `double` con redondeo a centavos.
