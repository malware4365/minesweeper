module com.minesweeper {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;

    opens com.minesweeper to javafx.fxml;
    exports com.minesweeper;
}
