package com.abcdel.tomasulo.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.lang.reflect.Field;

public class MainApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new Group(), 900, 600);
        stage.setTitle("Main Application");
        ScrollPane sp = new ScrollPane();
        sp.setStyle("-fx-background: rgb(80,80,80);");
        sp.prefHeightProperty().bind(scene.heightProperty());
        sp.prefWidthProperty().bind(scene.widthProperty());
        sp.setContent(createContentPane());
        ((Group) scene.getRoot()).getChildren().addAll(sp);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    private Node createContentPane() {
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10, 10, 10, 10));

        vbox.getChildren().add(createReserveStationTable());
        vbox.getChildren().add(createSecondaryTables());
        return vbox;
    }

    private Node createSecondaryTables() {
        final HBox hbox = new HBox();
        hbox.setSpacing(5);
        hbox.getChildren().add(createRecentlyUsedMemoryTable());
        return hbox;
    }

    private Node createRecentlyUsedMemoryTable() {
        final Label label = new Label("Recently Used Memory");
        label.setFont(new Font("Arial", 20));
        final VBox vbox = new VBox();
        TableView table = new TableView();
        table.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        table.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        table.setEditable(false);
        for (Field field : RecentlyUsedMemoryTableRow.class.getDeclaredFields()) {
            String fieldName = field.getName();
            TableColumn column = new TableColumn(fieldName.substring(1, fieldName.length()));
            column.setCellFactory(new PropertyValueFactory<ReserveStationTableRow, String>(fieldName));
            table.getColumns().add(column);
        }
        vbox.setSpacing(5);
        vbox.getChildren().addAll(label, table);
        return vbox;
    }

    private Node createReserveStationTable() {
        final Label label = new Label("Reserve Stations");
        label.setFont(new Font("Arial", 20));
        final VBox vbox = new VBox();
        TableView table = new TableView();
        table.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        table.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        table.setEditable(false);
        for (Field field : ReserveStationTableRow.class.getDeclaredFields()) {
            String fieldName = field.getName();
            TableColumn column = new TableColumn(fieldName.substring(1, fieldName.length()));
            column.setCellFactory(new PropertyValueFactory<ReserveStationTableRow, String>(fieldName));
            table.getColumns().add(column);
        }
        vbox.setSpacing(5);
        vbox.getChildren().addAll(label, table);
        return vbox;
    }
}

