package com.abcdel.tomasulo.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TableViewSample extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {
    Scene scene = new Scene(new Group());
    stage.setTitle("Main Application");
    stage.setWidth(900);
    stage.setHeight(600);

    BorderPane border = new BorderPane();
    border.setCenter(createGridPane());
    ((Group) scene.getRoot()).getChildren().addAll(border);
    stage.setScene(scene);
    stage.show();
  }

  private VBox createReserveStationTable(){
    final Label label = new Label("Estações de Reserva");
    label.setFont(new Font("Arial", 20));
    final VBox vbox = new VBox();
    TableView table = new TableView();
    table.setEditable(false);
    List<TableColumn> tableColumnsList = new ArrayList<TableColumn>();
    for (Field field : ReserveStationTableRow.class.getDeclaredFields()) {
      String fieldName = field.getName();
      TableColumn tableColumnName = new TableColumn(fieldName.substring(1,fieldName.length()));
      tableColumnName.setCellFactory(new PropertyValueFactory<ReserveStationTableRow, String>(fieldName));
      tableColumnsList.add(tableColumnName);
    }

    table.getColumns().addAll(tableColumnsList);
    vbox.setSpacing(5);
    vbox.setPadding(new Insets(10, 0, 0, 10));
    vbox.getChildren().addAll(label, table);
    return vbox;
  }

  private VBox createRecentlyUsedMemoryTable() {
    final Label label = new Label("Memória recentemente usada");
    label.setFont(new Font("Arial", 20));
    final VBox vbox = new VBox();
    TableView table = new TableView();
    table.setEditable(false);
    List<TableColumn> tableColumnList = new ArrayList<TableColumn>();
    tableColumnList.add(new TableColumn("teste1"));
    tableColumnList.add(new TableColumn("teste2"));
    table.getColumns().addAll(tableColumnList);
    vbox.setSpacing(5);
    vbox.setPadding(new Insets(10, 0, 0, 10));
    vbox.getChildren().addAll(label, table);
    return vbox;
  }

  private GridPane createGridPane(){
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(0, 10, 0, 10));

    grid.add(createReserveStationTable(), 1, 0);
    grid.add(createRecentlyUsedMemoryTable(), 1, 2);
    return grid;
  }

}

