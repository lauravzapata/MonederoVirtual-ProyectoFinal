# Validación realizada

Fecha: 11 de noviembre de 2026, hora de Colombia.

## Pruebas automatizadas

Se ejecutó `clean test` con Maven 3.8.5, JDK 21 y JavaFX 21.0.6.

**Resultado: 21 pruebas, 0 fallos, 0 errores, 0 omitidas.**

| Grupo | Cantidad | Casos cubiertos |
| --- | ---: | --- |
| MonederoControllerTest | 6 | Depósito y retiro, fondos insuficientes, transferencia y reversión, reversión sin fondos en receptor, programación única, documento duplicado |
| ReglasAvanzadasTest | 15 | Montos inválidos, umbral atípico, fronteras de rango, canje y reversión, historial del receptor, copias defensivas, recurrencias atrasadas, fallo y reintento, datos nulos y pertenencia, reversión repetida, bono y retiros gratis, descuentos combinados, centavos, orden cronológico y mismo monedero |

Para repetirlas, ejecutar `./mvnw test` o `mvnw.cmd test`. Maven genera resultados en `target/surefire-reports`.

## Prueba gráfica

Se cargó el FXML real y se renderizó una escena de 1120 × 840 usando JavaFX con un controlador gráfico sin pantalla (Monocle, utilizado solamente para la verificación en este entorno y no añadido a las dependencias del proyecto).

Se accionaron los botones de registrar cliente, depositar, consultar historial, retirar sin fondos y generar reporte. Se comprobaron el saldo visible, el contenido del historial y el mensaje de rechazo. La captura `docs/interfaz.png` proviene de esa ejecución.

Esta prueba verifica la carga FXML y esos flujos; no sustituye comprobar todos los controles en un escritorio de Windows o macOS. Para la revisión manual completa se incluye el recorrido del README. Las pruebas JUnit sí ejercitan las reglas de puntos, canjes, rangos y programación.

## Limitaciones conocidas

- Datos en memoria: no se verificó persistencia porque no se implementó.
- Sin mensajería externa: las alertas se almacenan y muestran dentro de la aplicación.
- La programación se procesa mediante el botón Ejecutar vencidas.
- El modelo usa `double` y redondea a centavos; es un simulador académico.
