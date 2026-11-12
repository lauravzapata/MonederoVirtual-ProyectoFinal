# Validación realizada

Fecha: 11 de noviembre de 2026, hora de Colombia.

## Pruebas automatizadas

Se ejecutó `clean test` con Maven 3.8.5, JDK 21 y JavaFX 21.0.6.

**Resultado actualizado: 40 pruebas JUnit, 0 fallos, 0 errores, 0 omitidas.**

Además, el recorrido JavaFX completó **48 comprobaciones de interfaz**. Los conteos son independientes: las comprobaciones gráficas no se incluyen en las 40 pruebas JUnit.

| Grupo | Cantidad | Casos cubiertos |
| --- | ---: | --- |
| MonederoControllerTest | 6 | Depósito y retiro, fondos insuficientes, transferencia y reversión, reversión sin fondos en receptor, programación única, documento duplicado |
| CasosLimiteTest | 19 | Saldo exacto con comisión, montos subcentavo, validación de clientes y monederos, edición y eliminación, atomicidad ante fondos insuficientes, reversiones, vencimiento de beneficios, multimonedero, desviación estadística, reportes, cancelación, fin de mes y fallos independientes de programadas |
| ReglasAvanzadasTest | 15 | Montos inválidos, umbral atípico, fronteras de rango, canje y reversión, historial del receptor, copias defensivas, recurrencias atrasadas, fallo y reintento, datos nulos y pertenencia, reversión repetida, bono y retiros gratis, descuentos combinados, centavos, orden cronológico y mismo monedero |

Para repetirlas, ejecutar `./mvnw test` o `mvnw.cmd test`. Maven genera resultados en `target/surefire-reports`.

## Prueba gráfica

Se cargó el FXML real y se renderizó una escena de 1120 × 840 usando JavaFX con un controlador gráfico sin pantalla (Monocle, utilizado solamente para la verificación en este entorno y no añadido a las dependencias del proyecto).

El recorrido `src/test/java/co/edu/uniquindio/poo/viewcontrollers/RecorridoInterfaz.java` pulsa los controles de las tres pestañas y comprueba sus resultados. Incluye:

- Registro y edición de clientes; creación y conservación de la selección de monederos.
- Depósitos, retiros, transferencias, historial del receptor y reversión de ambos saldos.
- Números inválidos, fondos insuficientes y reversión repetida.
- Reportes, los tres canjes, rechazo por falta de puntos y descuento ya activo.
- Historial de puntos y retiro sin comisión después del canje.
- Creación, ejecución y cancelación de programadas; rechazo de una segunda ejecución de la misma operación única.
- Notificaciones, confirmación de eliminación y protección del historial financiero.
- Limpieza de las selecciones de cliente y monedero después de eliminar un destinatario.

**Resultado: 48 comprobaciones aprobadas.** La ejecución usa el FXML y los eventos reales; no reemplaza el controlador por uno simulado.

Para repetir el recorrido en IntelliJ, ejecuta primero las pruebas Maven y luego el `main` de `RecorridoInterfaz`, desde `src/test/java`, con JDK 21 y las dependencias Maven de prueba. Se abren ventanas brevemente y las confirmaciones se aceptan automáticamente con datos ficticios; se imprime cada comprobación en consola. En este entorno sin pantalla se utilizó Monocle externo a las dependencias del proyecto.

### Problemas detectados y corregidos

1. **Saldo exacto y comisión:** $0,56 + $0,01 podía representarse como 0,5700000000000001 y rechazar un saldo de $0,57. El retiro normaliza a centavos antes de comparar. Se probaron tanto retiro como transferencia.
2. **Monto subcentavo:** un monto como 0,000000001 podía superar la tolerancia de validación y registrar una operación que no sumaba dinero. El controlador exige un mínimo de $0,01; el modelo también rechaza montos que redondean a cero.
3. **Selección obsoleta:** al eliminar un destinatario se restauraba su monedero anterior. El refresco descarta clientes eliminados y limpia explícitamente los monederos sin cliente.

### Evidencia guardada

- [Resumen de la ejecución JUnit](resultado-pruebas.txt).
- [Salida de las 48 comprobaciones gráficas](resultado-interfaz.txt).

La captura `docs/interfaz.png` corresponde al recorrido gráfico inicial. Esta comprobación se realizó en JavaFX para Linux; no se verificó la apariencia específica de Windows ni macOS. Ninguna suite garantiza todos los estados posibles del programa.

## Limitaciones conocidas

- Datos en memoria: no se verificó persistencia porque no se implementó.
- Sin mensajería externa: las alertas se almacenan y muestran dentro de la aplicación.
- La programación se procesa mediante el botón Ejecutar vencidas.
- El modelo usa `double` y redondea a centavos; es un simulador académico.
