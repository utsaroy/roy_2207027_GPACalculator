module com.utsa.advprog.roy_2207027_gpacalculator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens com.utsa.advprog.roy_2207027_gpacalculator to javafx.fxml;
    exports com.utsa.advprog.roy_2207027_gpacalculator;
}