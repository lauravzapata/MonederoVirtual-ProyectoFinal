# Guía para preparar la sustentación

## Recorrido del código

1. Abre `app/Aplicacion.java`: explica cómo carga el FXML con `FXMLLoader` y construye la escena.
2. Abre `inicio.fxml`, `clientes.fxml` y `monedero.fxml`: muestra `fx:controller`, un `fx:id` y un `onAction`.
3. Sigue `depositarEvent()` en `MonederoViewController`: recoge controles, llama al servicio y actualiza la pantalla.
4. Sigue `depositar()` en `MonederoController`: delega en `MonederoVirtual`. Abre el mismo método en el modelo para ver las validaciones, el análisis, el saldo y los puntos.
5. Compara `Deposito`, `Retiro` y `Transferencia`: señala `extends`, métodos sobrescritos y reversión.
6. Muestra la interfaz `IOperacion` y explica qué contrato impone.
7. Abre `esAtipica()`: explica media, varianza, desviación y por qué se usa el historial anterior.
8. Muestra `reporte()`: explica un filtro Stream y una suma o promedio con lambda.
9. Abre las pruebas: indica qué error evitan y por qué una operación fallida no debe cambiar el saldo.

## Preguntas que conviene poder responder

- ¿Por qué no se incluye la nueva transacción al calcular su umbral? Porque alteraría el comportamiento de referencia que se usa para evaluarla.
- ¿Por qué usar desviación poblacional? Se toma el historial disponible completo como población de referencia del simulador.
- ¿Por qué una recibida no da puntos? La regla recompensa al cliente que hace el envío y evita duplicar la recompensa.
- ¿Qué ocurre si el receptor ya gastó el dinero? La reversión falla sin devolver dinero al emisor.
- ¿Qué ocurre si ya canjeó los puntos? La reversión se rechaza si restarlos deja puntos negativos.
- ¿Qué ocurre con una recurrente atrasada? Se procesan sus vencimientos en orden junto a las demás operaciones. Un fallo se deja para un siguiente intento.
- ¿Un canje cambia el rango? Sí: se usa el saldo de puntos disponibles.
- ¿Cuál es la complejidad del detector? Tiempo O(n) y espacio adicional O(1): se recorre una vista no modificable del historial, sin copiar los elementos.
- ¿Qué pasa al cerrar? Se pierden los datos porque están en memoria.
- ¿Se envían mensajes de verdad? No: se muestran alertas internas; es una diferencia explícita frente a la mención de servicios externos del enunciado.

Repite el recorrido del README con tus propios datos y asegúrate de poder justificar cada regla antes de entregar.

## Relación con tu parcial de panadería

- `Panaderia` → `MonederoVirtual`: listas y reglas en el modelo.
- `PedidoController`/`EmpleadoController` → `MonederoController`/`ClienteController`: reciben el modelo y delegan.
- `Aplicacion.openViewPedidos()` → `Aplicacion.openViewMonedero()`: cargar FXML, obtener `ViewController`, llamar `setApp(this)` y mostrar escena.
- `IProduccion` → `IOperacion`: contrato mediante interfaz.
- `PanaderoArtesanal`/`PanaderoIndustrial` → `Deposito`/`Retiro`/`Transferencia`: herencia y comportamiento sobrescrito.
- `Collections.unmodifiableList` protege listas en ambos proyectos.

Al volver a Inicio no se crea otro modelo. Por eso los clientes, saldos y puntos siguen disponibles al regresar a Monedero.
