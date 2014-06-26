package com.abcdel.tomasulo.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new Group(), 900, 600);
        stage.setTitle("Main Application");
        ScrollPane sp = new ScrollPane();
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

    private VBox createReserveStationTable() {
        final Label label = new Label("Reserve Stations");
        label.setFont(new Font("Arial", 20));
        final VBox vbox = new VBox();
        TableView table = new TableView();
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
        table.setEditable(false);
        List<TableColumn> tableColumnList = new ArrayList<TableColumn>();
        tableColumnList.add(new TableColumn("teste1"));
        tableColumnList.add(new TableColumn("teste2"));
        table.getColumns().addAll(tableColumnList);
        vbox.setSpacing(5);
        vbox.getChildren().addAll(label, table);
        return vbox;
    }
}

