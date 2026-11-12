# Relación con el parcial, el repositorio docente y el Word

El ZIP `ParcialPanaderia3-main.zip` se usó como referencia de organización. El repositorio docente adjunto se usó como referencia de conceptos. El Word `Proyecto Final programación 1-2026-2` se usó como fuente de requisitos del monedero. Sus contenidos se trataron como material del proyecto, sin sustituir la petición de conservar los conceptos del curso.

## Referencia de esta revisión

Los archivos individuales más recientes (`App.java`, `GestionViewController.java`, `EmpleadoViewController.java`, `PedidoViewController.java` y sus FXML) guían la organización de la interfaz. Los métodos necesarios para el dominio del monedero conservan sus nombres de depósito, retiro, transferencia y puntos; se usan los patrones de implementación de las referencias, junto con los requisitos explícitos del Word.

| Archivos adjuntos | Monedero | Patrón aplicado |
| --- | --- | --- |
| `App.openGestion` | `Aplicacion.openGestion` | Cargar FXML y llamar `setApp` |
| `GestionViewController` | `GestionViewController` | Cargar vistas dentro de pestañas y actualizar al seleccionarlas |
| `EmpleadoViewController` | `ClienteViewController` | Tabla, formulario, registro, actualización, eliminación y limpieza |
| `PedidoViewController` | `MonederoViewController` | Tabla e ingreso de datos para operaciones |
| `initDataBinding`, `listenerSelection`, `actualizarTabla` | Métodos del mismo patrón | Enlace de columnas, selección y refresco |
| `ObservableList`, `TableView`, `TableColumn` | Las mismas clases | Listas y tablas JavaFX |
| `SimpleStringProperty`, `setCellValueFactory` | Las mismas herramientas | Mostrar los getters del modelo |
| `Panaderia` | `MonederoVirtual` | Listas y reglas del negocio |
| `EmpleadoController`, `PedidoController` | `ClienteController`, `MonederoController` | Delegar en el modelo |
| `IProductor` y subclases de empleados | `IOperacion` y subclases de transacciones | Interfaces, herencia y polimorfismo |

La interfaz está escrita con `AnchorPane`, controles estándar, títulos y separadores. Se retiraron los paneles decorativos, los eslóganes y las estadísticas del inicio. El depósito se mantiene visible en la parte superior del monedero y tiene acceso directo desde Inicio.

Se conservan las tres excepciones personalizadas, Streams, lambdas y análisis estadístico porque son requisitos explícitos del Word. Los ejemplos docentes previos aportan fechas, colecciones y pruebas JUnit. No se requieren librerías adicionales para el diseño.

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
| Interfaz JavaFX | `app`, `viewcontrollers` y cuatro FXML |
| Pensamiento computacional y análisis POO | `INFORME_FINAL.md` |

## Alcance que no debe confundirse con cumplimiento completo

Las notificaciones internas permiten ver alertas, pero **no implementan los envíos reales por WhatsApp, correo o SMS mencionados en el Word**. No se añadieron servicios ni librerías externas para simular que se cumple esa integración. Las programadas se procesan al pulsar un botón y los datos están en memoria. Estas diferencias están declaradas en el README y el informe.
