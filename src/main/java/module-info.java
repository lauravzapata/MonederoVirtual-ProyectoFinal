module co.edu.uniquindio.poo.monedero {
    requires javafx.controls;
    requires javafx.fxml;

    opens co.edu.uniquindio.poo.monedero.viewcontrollers to javafx.fxml;
    exports co.edu.uniquindio.poo.monedero.app;
    exports co.edu.uniquindio.poo.monedero.model;
    exports co.edu.uniquindio.poo.monedero.controller;
    exports co.edu.uniquindio.poo.monedero.exceptions;
}
