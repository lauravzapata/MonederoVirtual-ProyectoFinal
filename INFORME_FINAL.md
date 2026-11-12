# Informe final · Monedero Virtual con sistema de puntos

## Objetivo y alcance

Simular cuentas virtuales con varios monederos, depósitos, retiros, transferencias, puntos, beneficios, rangos, programación de movimientos, reportes y detección estadística de operaciones atípicas. La aplicación usa JavaFX y FXML con la estructura del parcial `ParcialPanaderia3` y del ejemplo docente `empresa`.

El documento adjunto se tomó como fuente de requisitos académicos. Las decisiones de implementación se ajustaron a la petición de usar los conceptos del repositorio docente; no se incorporaron integraciones externas ni frameworks adicionales. El README distingue funciones completas y diferencias pendientes frente al enunciado.

## Pensamiento computacional

**Descomposición.** Se separó el problema en clientes, monederos, transacciones, puntos, canjes, programadas, alertas y reportes. `Aplicacion` arranca la aplicación y abre Inicio, Clientes o Monedero. Los controladores de vista atienden eventos; `ClienteController` y `MonederoController` delegan las reglas en `model/MonederoVirtual`, igual que los controladores del parcial delegan en `Panaderia`.

**Reconocimiento de patrones.** Todas las transacciones tienen código, cliente, monto, fecha y estado, pero difieren en puntos y reversión. Las programadas comparten fecha, frecuencia y conteo de ejecuciones. Este patrón permite recorrer colecciones heterogéneas sin repetir la lógica común.

**Abstracción.** `Transaccion` reúne los datos comunes y declara el comportamiento variable. `IOperacion` expresa el contrato de monto, tipo y cálculo de puntos. La interfaz permite operar sin mostrar cómo se almacenan las listas o se calcula la desviación.

**Codificación.** Se tradujeron las reglas a métodos pequeños, con validaciones antes de modificar saldos. Los eventos de FXML delegan en el controlador y muestran mensajes comprensibles. Se contrastaron reglas con pruebas JUnit y se cargó el FXML en JavaFX para comprobar flujos básicos.

## Aplicación de POO

| Principio | Código concreto | Aporte |
| --- | --- | --- |
| Abstracción | `Transaccion` abstracta y `IOperacion` | Definen atributos y comportamientos comunes |
| Encapsulamiento | Atributos privados, `depositar`, `retirar`, `Collections.unmodifiableList` en getters | Impide modificar las listas desde la vista o los controladores |
| Herencia | `Deposito`, `Retiro`, `Transferencia`, `TransferenciaRecibida` extienden `Transaccion` | Reutiliza código, fecha, estado y presentación |
| Polimorfismo | `calcularPuntos()`, `getTipo()`, `revertir()` | La misma llamada ejecuta reglas distintas según el objeto |
| Interfaces | `Transaccion implements IOperacion` | Establece un contrato común de operaciones |

`TransferenciaRecibida` refleja la operación original en el receptor. No genera puntos, no se revierte independientemente y consulta el estado de la transferencia original. Se evita duplicar el dinero o premiar dos veces el mismo envío.

Las entidades mantienen métodos de modificación usados por el modelo central; esto es una separación sencilla de responsabilidades, no una capa de seguridad contra código malicioso.

## Estructuras y programación declarativa

Se usan `ArrayList` para clientes, monederos, transacciones, movimientos de puntos y programadas. Es la colección utilizada en los ejemplos docentes. La búsqueda de clientes usa `stream().filter(...).findFirst()`. Los reportes usan `filter`, `mapToDouble`, `sum`, `average`, `max` y `count`. La ordenación de programadas usa `sort` con lambda por fecha y sitúa las finalizadas al final.

## Detección de operaciones atípicas

Antes de registrar el movimiento nuevo se consideran los montos del historial anterior que no estén revertidos, incluidas transferencias recibidas. Se exige un mínimo de dos observaciones para evitar decisiones con un único antecedente.

1. Calcular `media = suma / n`.
2. Calcular `varianza = suma((monto - media)²) / n`.
3. Obtener `desviación = sqrt(varianza)`.
4. Marcar si `nuevoMonto > media + 2 × desviación`.

Por ejemplo, antecedentes 100 y 100 producen media 100 y desviación 0. Un monto de 100 no se marca; uno de 101 sí. La alerta significa que merece revisión; no demuestra fraude y no bloquea la operación.

El cálculo realiza dos recorridos: **tiempo O(n)**. El getter entrega una vista no modificable del historial, sin copiar sus elementos. Las variables estadísticas y esa vista ocupan **espacio adicional O(1)**. Se sigue el patrón de `Collections.unmodifiableList` del parcial de panadería. Excluir reversiones evita usar movimientos anulados como actividad habitual. El nuevo monto se evalúa antes de incorporarlo para no alterar su propio umbral. El estado de sospecha queda como registro de la evaluación original y no se reescribe después de otras reversiones.

## Programación y ordenamiento

Se ordena por fecha ascendente. Se toma la primera operación vencida que no haya fallado en esta ejecución, se procesa y se avanza su fecha. Se vuelve a ordenar para intercalar correctamente las recurrencias con las demás programadas. Si falla por saldo insuficiente, conserva su fecha y se excluye de ese lote, evitando un ciclo infinito; se reintenta en una próxima pulsación.

Con `p` programadas y `e` ejecuciones vencidas, ordenar repetidamente cuesta aproximadamente **O((e+p) × p log p)**, más búsquedas y el análisis de historial de cada operación. Es una solución sencilla apropiada para una simulación académica con listas, no para grandes volúmenes. El conteo de ejecuciones y la fecha próxima evitan duplicados al repetir la consulta para el mismo día.

## Reglas de dinero, puntos y beneficios

Las operaciones comprueban pertenencia del monedero, monto positivo finito con dos decimales y saldo suficiente. Las comisiones base son 1% y se redondean a centavos. Los puntos se dan por centenas completas: 1 depósito, 2 retiro y 3 transferencia enviada. Se suman movimientos de puntos, incluidos débitos por canje y reversión; el rango se deriva de ese total.

La reversión verifica primero que no sea repetida y que no deje puntos negativos. El dinero se resta del receptor antes de restaurar el del emisor, para rechazar una reversión sin fondos sin modificar el emisor. Los beneficios por rango y los porcentajes están detallados en el README. Las colecciones devueltas son vistas no modificables, aunque los objetos siguen compartidos como en el parcial de panadería.

El uso de `double` con redondeo conserva las herramientas numéricas básicas de los ejemplos. Una aplicación bancaria real requeriría una representación monetaria y persistencia transaccional más estrictas.

## Excepciones y validaciones

- `DatoInvalidoException`: datos vacíos, documento repetido, monto inválido, monedero ajeno, frecuencia inválida, canje sin puntos o reversión no permitida.
- `SaldoInsuficienteException`: retiro, transferencia o reversión sin fondos.
- `CuentaNoEncontradaException`: cliente o código inexistente.

`MonederoVirtual` valida antes de alterar el estado. La vista captura los errores y los presenta en el panel de resultados; un número mal escrito muestra un mensaje específico. La eliminación pide confirmación y se rechaza si hay historial financiero, fondos o programadas asociadas.

## Interfaz

Un menú de Inicio abre Clientes o Monedero; ambas pantallas tienen botón Volver al inicio. `Aplicacion` carga cada FXML con `FXMLLoader`, obtiene el `ViewController` y llama a `setApp(this)`. La aplicación conserva una sola instancia de `MonederoVirtual`. En la pantalla Monedero, la selección de cliente y monedero permanece visible. Las pestañas separan movimientos, puntos y programadas; el panel inferior conserva resultados y alertas. Las propiedades de FXML controlan tipografía, colores de texto, separación y tamaños. No se añadieron CSS ni librerías visuales.

## Verificación y límites

Se incluyen pruebas automatizadas para operaciones básicas y avanzadas, rangos, canjes, reversiones, alertas, recurrencias, validaciones y comisiones. Los resultados concretos y la prueba gráfica están en `docs/PRUEBAS.md`.

No hay persistencia, autenticación ni envíos externos de mensajes. El procesamiento de programadas se inicia con un botón. El informe reconoce estas diferencias frente al objetivo del documento; no las presenta como funcionalidades terminadas. Debe consultarse al docente la aceptación de alertas internas en lugar de WhatsApp, correo o SMS.
