# Monedero virtual

Aplicación JavaFX con clientes, depósitos, retiros, transferencias, puntos y transacciones programadas. La estructura y las tablas siguen los archivos adjuntos de panadería.

![Monedero](docs/interfaz.png)

[Inicio](docs/inicio.png) · [Clientes](docs/clientes.png) · [Guía para abrir el proyecto](docs/ABRIR_PROYECTO.md)

## Ejecutar

Abre `pom.xml` como proyecto Maven en IntelliJ. Selecciona **JDK 21** para el proyecto y Maven.

Windows:

```powershell
.\mvnw.cmd clean javafx:run
.\mvnw.cmd test
```

Linux/macOS:

```bash
./mvnw clean javafx:run
./mvnw test
```

## Ingresar dinero

1. Abre **Clientes** y registra documento y nombre.
2. En la misma ventana, abre la pestaña **Monedero**. Se seleccionan el primer cliente disponible y su monedero Principal; puedes elegir otro cliente o monedero.
3. Escribe el monto en **Depósitos y retiros** y pulsa **Depositar dinero**.
4. El saldo mostrado corresponde al monedero seleccionado. El depósito aparece en la tabla de Historial y genera puntos.

También hay un acceso **Depositar dinero** desde Inicio. Si no hay clientes registrados, el resultado indica que debes registrar uno. El botón de depósito y el monto permanecen visibles en todas las pestañas de operaciones. Los decimales admiten punto o coma, sin separadores de miles.

## Recorrido de las operaciones

- **Clientes:** registrar; seleccionar una fila para actualizar el nombre o eliminar; Limpiar para ingresar otro cliente. El documento no se modifica.
- **Monedero:** seleccionar cliente y monedero, crear otro monedero y consultar saldo, puntos y rango.
- **Depósitos y retiros:** escribir el monto y pulsar Depositar dinero o Retirar.
- **Transferencias:** usar el mismo monto; elegir cliente y monedero de destino y pulsar Transferir.
- **Historial:** seleccionar una fila para completar el código de reversión y pulsar Revertir transacción.
- **Puntos y beneficios:** canjear 100, 500 o 1000 puntos, o consultar el historial de puntos.
- **Programadas:** usar monto y destino de los campos superiores; elegir tipo, fecha y frecuencia. Pulsar Ejecutar vencidas para procesarlas.
- **Reportes:** consultar gastos y notificaciones.

Ejemplo: deposita 1000 en Ana (101), transfiere 200 a Luis (102) y comprueba los saldos 798 y 200, incluida comisión de 2. Revierte la transferencia desde Ana para restaurar ambos saldos.

## Organización

```text
src/main/java/co/edu/uniquindio/poo/monedero/
├── app/Aplicacion.java
├── model/                  MonederoVirtual y entidades
├── controller/             ClienteController y MonederoController
├── viewcontrollers/
│   ├── InicioViewController.java
│   ├── GestionViewController.java
│   ├── ClienteViewController.java
│   └── MonederoViewController.java
└── exceptions/             Tres excepciones del negocio
src/main/resources/co/edu/uniquindio/poo/monedero/
├── inicio.fxml
├── gestion.fxml
├── clientes.fxml
└── monedero.fxml
```

`Aplicacion` conserva el modelo compartido. `GestionViewController` carga dos FXML dentro de pestañas, como el archivo adjunto del mismo nombre. Los controladores de las tablas se inicializan con el modelo de la aplicación. `initDataBinding`, `listenerSelection`, `ObservableList`, `TableView`, `SimpleStringProperty`, `setCellValueFactory` y `actualizarTabla` siguen los patrones de `EmpleadoViewController` y `PedidoViewController` adjuntos.

Las reglas permanecen en `MonederoVirtual`; los controladores delegan en el modelo. Se mantienen las lambdas, Streams, excepciones personalizadas y POO exigidos en el Word. La interfaz usa controles FXML sencillos y textos funcionales.

## Reglas definidas

| Operación | Puntos por cada 100 unidades completas | Comisión base |
| --- | ---: | ---: |
| Depósito | 1 | 0% |
| Retiro | 2 | 1% |
| Transferencia enviada | 3 | 1% |
| Transferencia recibida | 0 | 0% |

Los montos admiten dos decimales, un mínimo de un centavo y un máximo de mil millones por operación; se rechazan cero, negativos, infinito y NaN. Se redondean saldo y comisiones a centavos usando las herramientas numéricas básicas del curso.

| Rango, según puntos disponibles | Intervalo | Reducción de comisión de transferencia |
| --- | --- | ---: |
| Bronce | 0–500 | 0% |
| Plata | 501–1000 | 5% |
| Oro | 1001–5000 | 10% |
| Platino | Más de 5000 | 20% |

- **100 puntos:** descuento permanente del 10% sobre la comisión de transferencia después del beneficio de rango. No se cobra un segundo canje si ya está activo.
- **500 puntos:** un mes sin comisión de retiros. Otro canje extiende un mes desde la fecha de vencimiento si sigue vigente.
- **1000 puntos:** bono de 50 en el monedero seleccionado. El bono no genera puntos nuevos.
- **Reversión:** resta los puntos originales y devuelve el dinero y la comisión. Se rechaza si el dinero necesario ya se gastó, si ya fue revertida o si dejaría puntos negativos por canjes previos. Solo el emisor revierte una transferencia.
- **Programadas:** única, semanal o mensual; se procesan todas las ocurrencias vencidas en orden cronológico al pulsar Ejecutar vencidas. Un fallo mantiene la fecha para reintentar en otra pulsación y genera una notificación. No impide procesar las demás operaciones.
- **Alertas:** saldo inferior a 100, programación creada, transferencia recibida y operaciones atípicas.
- **Análisis:** compara el nuevo monto con `promedio + 2 × desviación estándar poblacional` del historial anterior no revertido. Exige al menos dos movimientos anteriores y usa una comparación estricta `>`.

Las tarifas, los beneficios adicionales por rango y el tratamiento de canjes previos a una reversión son decisiones documentadas, pues el enunciado no fija esos detalles.

## Alcance y diferencias frente al enunciado

Los datos viven **en memoria** y se reinician al cerrar. Las alertas son **internas**; no se envían correos, WhatsApp ni SMS. Las programadas requieren pulsar el botón y no corren con la aplicación cerrada. Estos aspectos deben confirmarse con el docente: la mención de notificaciones externas del enunciado no está implementada como integración real.

Las transacciones periódicas usan `plusWeeks` o `plusMonths`; en fin de mes Java ajusta al último día válido. La clasificación usa puntos disponibles, por lo que un canje puede reducir el rango. El historial de puntos conserva ganancias, canjes y reversiones.

## Documentación y validación

- [Informe final](INFORME_FINAL.md): pensamiento computacional, POO, algoritmos y complejidad.
- [Pruebas realizadas](docs/PRUEBAS.md): resultados y recorrido de interfaz.
- [Guía de sustentación](docs/SUSTENTACION.md): cómo explicar las decisiones y ubicar el código.
