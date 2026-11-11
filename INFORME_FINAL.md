# Informe técnico - Monedero Virtual

## Pensamiento computacional
**Descomposición:** dividir el sistema en gestión de clientes, monederos, operaciones, puntos, programación, alertas y reportes.

**Reconocimiento de patrones:** depósitos, retiros y transferencias comparten atributos: código, cliente, monto, fecha, comisión y estado. Cada uno tiene sus propias reglas de reversión y puntuación.

**Abstracción:** la clase abstracta `Transaccion` contiene los elementos comunes y la interfaz `Operacion` describe las operaciones que deben ofrecer las transacciones.

**Codificación:** `MonederoController` realiza las validaciones y cálculos; `PrincipalViewController` interpreta los botones; `principal.fxml` define la ventana; `Main` inicia la aplicación.

## Principios POO
- Encapsulamiento: atributos privados y operaciones mediante métodos públicos.
- Herencia: `Deposito`, `Retiro` y `Transferencia` extienden `Transaccion`.
- Polimorfismo: cada tipo implementa su propio `getTipo()`, `calcularPuntos()` y `revertir()`.
- Abstracción: `Transaccion` abstracta e interfaz `Operacion`.

## Uso de Stream y lambda
- Búsqueda de cliente: filtro con lambda y `findFirst()`.
- Reporte: filtros y `mapToDouble().sum()`.
- Ordenamiento programado: `sort()` con lambda por fecha.

## Validaciones y excepciones
- `DatoInvalidoException`: datos vacíos, monto incorrecto, reversión repetida o duplicados.
- `SaldoInsuficienteException`: operación sin fondos, incluidas reversiones que necesitan retirar dinero del receptor.
- `CuentaNoEncontradaException`: buscar código de transacción o cliente inexistente.

## Algoritmo de transacciones atípicas
Para cada nueva operación se calcula el promedio de los montos anteriores no revertidos; luego la desviación estándar poblacional. Se marca como sospechosa si `monto > promedio + 2 * desviación`. Requiere al menos dos operaciones anteriores. Dos recorridos del historial dan **tiempo O(n)** y un conjunto fijo de variables da **espacio adicional O(1)**. Se evalúa el monto antes de agregar la nueva operación para que no cambie su propio umbral.

## Pruebas
Se incluyen pruebas JUnit 5: depósito, retiro, saldo insuficiente, transferencia, reversión, reversión imposible, programación única y documentos duplicados. Ejecutarlas con `mvn test` y guardar capturas de resultados para la entrega. Probar también manualmente los controles JavaFX.

## Limitaciones y decisiones explícitas
El enunciado no fija tarifas de comisión; se eligió 1% para visualizar los beneficios. Los datos están en memoria. Las notificaciones son internas y no envíos externos a WhatsApp, Gmail o SMS; las programadas se ejecutan manualmente mediante el botón en este simulador de escritorio. Estas limitaciones se deben aclarar en la sustentación.
