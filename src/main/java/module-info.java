module org.unibl.etf{
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.logging;

    exports org.unibl.etf.controllers;
    opens org.unibl.etf.controllers to javafx.fxml;
    exports org.unibl.etf;
    opens org.unibl.etf to javafx.fxml;
}