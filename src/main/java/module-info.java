module org.unibl.etf.javaproject2022 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens org.unibl.etf.javaproject2022 to javafx.fxml;
    exports org.unibl.etf.javaproject2022;
}