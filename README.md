# Monedero Virtual con Sistema de Puntos

Proyecto final de Programación I - Universidad del Quindío. Java 17, JavaFX y FXML.

## Organización
- `app/Main`: inicia JavaFX.
- `model`: clases Cliente, Monedero, Transaccion abstracta, Deposito, Retiro, Transferencia, MovimientoPuntos, TransaccionProgramada e interfaz Operacion.
- `controller/MonederoController`: reglas y validaciones.
- `viewcontrollers/PrincipalViewController`: conecta botones FXML con operaciones.
- `resources/.../fxml/principal.fxml`: interfaz gráfica, **sin CSS**.
- `exceptions`: tres excepciones de negocio.
- `src/test`: pruebas de reglas básicas.

## Ejecutar en IntelliJ IDEA
1. Abre la carpeta como un proyecto Maven.
2. Usa JDK 17 o superior.
3. Espera a que Maven descargue las dependencias.
4. En la terminal del proyecto ejecuta `mvn clean javafx:run` o inicia la clase `app.Main` con JavaFX configurado.
5. Para pruebas, `mvn test`.

## Cómo probar
1. Registra dos clientes, por ejemplo Ana (101) y Luis (102).
2. Selecciona Ana, deposita 1000 y revisa su saldo, historial y puntos.
3. Selecciona Luis como destino y realiza una transferencia de 200.
4. Prueba un retiro mayor al saldo para ver la excepción de negocio.
5. Usa el código del historial para revertir; prueba también la reversión cuando el receptor ya no tiene saldo.
6. Programa una transferencia con fecha de hoy y pulsa **Ejecutar vencidas**.
7. Revisa los informes y las notificaciones.

## Reglas del simulador
- Se acumulan 1, 2 y 3 puntos por cada 100 unidades completas en depósitos, retiros y transferencias, respectivamente.
- Rangos: Bronce 0-500, Plata 501-1000, Oro 1001-5000 y Platino más de 5000.
- El enunciado propone beneficios y rangos, pero no fija las tarifas de comisión. Para permitir demostrar los descuentos, **se eligió una comisión del 1% para retiros y transferencias**.
- 100 puntos: descuento permanente del 10% sobre la comisión de transferencia; 500 puntos: retiros sin comisión durante un mes desde el canje; 1000 puntos: bono de 50 unidades en el monedero seleccionado.
- La reversión deshace el efecto del dinero y resta los puntos generados. Si no hay saldo suficiente para deshacer un depósito o transferencia, se rechaza la reversión sin alterar el historial.
- Las operaciones periódicas se activan **al pulsar Ejecutar vencidas**. Las frecuencias son Única, Semanal y Mensual. En una pulsación se ejecuta una vez cada programación vencida; las operaciones mensuales se calculan con `plusMonths(1)`.
- El análisis de monto atípico usa el historial anterior no revertido: `promedio + 2 * desviación estándar poblacional`. Exige al menos dos operaciones anteriores. Tiempo O(n) y espacio adicional O(1).
- Las alertas son **internas**, visibles en la sección de notificaciones. No hay integración real con WhatsApp, Gmail o SMS.
- No se utiliza base de datos ni guardado en archivos: la información se reinicia cuando se cierra la aplicación.

## Relación con el curso
La organización se basa en el ejemplo `empresa` del repositorio docente: `app`, `model`, `controller`, `viewcontrollers` y FXML. Se usan `ArrayList`, `for`, `if`, `try/catch`, getters/setters, clases abstractas, `extends`, `implements`, excepciones, expresiones lambda y Stream API por exigencia del enunciado. La interfaz usa controles JavaFX y sus propiedades FXML, sin hojas CSS ni estilos CSS.

## Pendiente para evaluación
Verificar con el docente si las notificaciones deben enviarse realmente a servicios externos y si exige almacenamiento persistente. La interfaz y el código deben probarse en IntelliJ con JavaFX configurado.
