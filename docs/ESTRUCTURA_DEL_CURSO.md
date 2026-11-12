# Relación con el parcial, el repositorio docente y el Word

El ZIP `ParcialPanaderia3-main.zip` se usó como referencia de organización. El repositorio docente adjunto se usó como referencia de conceptos. El Word `Proyecto Final programación 1-2026-2` se usó como fuente de requisitos del monedero. Sus contenidos se trataron como material del proyecto, sin sustituir la petición de conservar los conceptos del curso.

## Misma separación de responsabilidades

| Parcial de panadería | Monedero final | Responsabilidad |
| --- | --- | --- |
| `app/Aplicacion` | `app/Aplicacion` | Conserva el modelo, carga FXML y cambia la escena |
| `model/Panaderia` | `model/MonederoVirtual` | Listas, búsquedas, reglas y validaciones |
| `controller/EmpleadoController` | `controller/ClienteController` | Delega registro, edición y consulta de clientes |
| `controller/PedidoController` | `controller/MonederoController` | Delega operaciones del monedero |
| `InicioViewController` | `InicioViewController` | Botones del menú inicial |
| `EmpleadoViewController` | `ClienteViewController` | Datos de controles y eventos de clientes |
| `PedidoViewController` | `MonederoViewController` | Datos de controles y eventos de operaciones |
| `setApp(Aplicacion)` | `setApp(Aplicacion)` | Recibe la aplicación y construye el controlador con el modelo compartido |
| `inicio`, `empleados`, `pedidos` | `inicio.fxml`, `clientes.fxml`, `monedero.fxml` | Una vista FXML por pantalla |
| `IProduccion`, `Panadero` y subclases | `IOperacion`, `Transaccion` y subclases | Interfaz, abstracción, herencia y polimorfismo |
| `List`, `ArrayList`, `Collections.unmodifiableList` | Las mismas herramientas | Guardar y proteger colecciones |
| `PanaderiaTest` en `model` | `MonederoVirtualTest`, `ReglasAvanzadasTest`, `CasosLimiteTest` en `model` | Pruebas donde viven las reglas |

Las fuentes Java tienen extensión `.java` y las vistas `.fxml`. Se usa `module-info.java`, que abre el paquete de controladores de vista a `javafx.fxml`. No se copiaron los nombres de archivo sin extensión ni la extensión `.fmxl` del ZIP, porque impedirían la compilación o la carga normal de las vistas.

## Conceptos que aparecen en los materiales

- Ejemplo docente `empresa`: `Aplicacion`, `FXMLLoader`, `setApp`, `controller`, `viewcontrollers`, búsqueda con Stream y lambda.
- Parcial de panadería: modelo central, constructores de controladores con ese modelo, `List`/`ArrayList`, vistas no modificables, validaciones, navegación con Volver, interfaces y clases abstractas.
- Ejemplos docentes `envios` y `Tienda`: filtros, búsquedas, colecciones, fechas y pruebas JUnit.
- Word: exige explícitamente Stream API, lambdas, al menos tres excepciones personalizadas, JavaFX, POO y detección de atípicas con media y desviación estándar.

## Requisitos del Word y ubicación

| Requisito | Ubicación principal |
| --- | --- |
| Depósitos, retiros y transferencias | `MonederoVirtual` y clases `Deposito`, `Retiro`, `Transferencia` |
| Saldo, historial y varios monederos | `Cliente`, `Monedero`, `MonederoViewController` |
| Puntos, rangos y canjes | `Cliente`, `MovimientoPuntos`, `MonederoVirtual.canjear` |
| Recalcular puntos al revertir | `MonederoVirtual.revertir` y métodos polimórficos `revertir` |
| Programación y ordenamiento por fecha | `TransaccionProgramada`, `ejecutarPendientes`, `ordenarProgramadas` |
| Reportes y patrones de gasto | `MonederoVirtual.reporte`, filtros y cálculos con Streams |
| Detector de atípicas | `MonederoVirtual.esAtipica`, explicado en `INFORME_FINAL.md` |
| Tres excepciones propias | Paquete `exceptions` |
| Interfaz JavaFX | `app`, `viewcontrollers` y tres FXML |
| Pensamiento computacional y análisis POO | `INFORME_FINAL.md` |

## Alcance que no debe confundirse con cumplimiento completo

Las notificaciones internas permiten ver alertas, pero **no implementan los envíos reales por WhatsApp, correo o SMS mencionados en el Word**. No se añadieron servicios ni librerías externas para simular que se cumple esa integración. Las programadas se procesan al pulsar un botón y los datos están en memoria. Estas diferencias están declaradas en el README y el informe.
