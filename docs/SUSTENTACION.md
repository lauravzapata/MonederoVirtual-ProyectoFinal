# Guía para preparar la sustentación

## Recorrido del código

1. Abre `app/Main.java`: explica cómo carga el FXML con `FXMLLoader` y construye la escena.
2. Abre `principal.fxml`: muestra `fx:controller`, un `fx:id` y un `onAction`.
3. Sigue `depositar()` en `PrincipalViewController`: recoge controles, llama al servicio y actualiza la pantalla.
4. Sigue `depositar()` en `MonederoController`: valida, analiza el monto, modifica saldo y registra puntos.
5. Compara `Deposito`, `Retiro` y `Transferencia`: señala `extends`, métodos sobrescritos y reversión.
6. Muestra la interfaz `Operacion` y explica qué contrato impone.
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
- ¿Cuál es la complejidad del detector? Tiempo O(n) y espacio O(n) por la copia defensiva del historial; las variables estadísticas por sí solas ocupan O(1).
- ¿Qué pasa al cerrar? Se pierden los datos porque están en memoria.
- ¿Se envían mensajes de verdad? No: se muestran alertas internas; es una diferencia explícita frente a la mención de servicios externos del enunciado.

Repite el recorrido del README con tus propios datos y asegúrate de poder justificar cada regla antes de entregar.
