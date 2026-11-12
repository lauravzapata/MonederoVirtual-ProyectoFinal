# Abrir la versión completa

Si aparecen pantallas blancas o botones sin respuesta después de copiar archivos sueltos, abre una copia completa del proyecto para que los FXML y sus controladores correspondan a la misma versión.

1. Descarga el ZIP desde GitHub: **Code → Download ZIP**.
2. Extrae el ZIP en una carpeta nueva. Conserva tu copia anterior si tiene cambios propios.
3. En IntelliJ abre el archivo `pom.xml` como proyecto Maven y selecciona **JDK 21** para el proyecto y Maven.
4. Espera a que se descarguen las dependencias y abre la terminal en la carpeta que contiene `pom.xml`.
5. En Windows ejecuta:

```powershell
.\mvnw.cmd clean javafx:run
```

En Linux/macOS:

```bash
./mvnw clean javafx:run
```

Se debe abrir Inicio con Clientes, Depositar dinero y Monedero. En Clientes registra documento y nombre; abre la pestaña Monedero, escribe el monto y pulsa Depositar dinero. El saldo se actualiza en esa misma pantalla.

## Archivos que deben corresponder

- `app/Aplicacion.java`: carga Inicio y Gestión, obtiene sus controladores y llama a `setApp(this)`.
- `viewcontrollers/InicioViewController.java`: eventos `abrirClientes` y `abrirMonedero`.
- `viewcontrollers/GestionViewController.java`: carga Clientes y Monedero en las pestañas.
- `viewcontrollers/ClienteViewController.java` y `MonederoViewController.java`: formularios y tablas con el modelo compartido.
- `src/main/resources/co/edu/uniquindio/poo/monedero/`: los cuatro FXML completos.

Cambiar únicamente `fx:controller` en una plantilla vacía no agrega los botones. Copiar un FXML sin su controlador actualizado puede causar errores al cargarlo o al pulsar sus controles. Si falla esta copia completa, conserva el mensaje completo de la consola para identificar el archivo y la línea del error.
