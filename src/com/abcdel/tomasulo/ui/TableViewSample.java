package com.abcdel.tomasulo.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TableViewSample extends Application {

  private TableView table = new TableView();
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {
    Scene scene = new Scene(new Group());
    stage.setTitle("Main Application");
    stage.setWidth(900);
    stage.setHeight(500);

    final Label label = new Label("Estações de Reserva");
    label.setFont(new Font("Arial", 20));

    table.setEditable(true);
    List<TableColumn> tableColumnsList = new ArrayList<TableColumn>();
    for (Field field : ReserveStationTableRow.class.getDeclaredFields()) {
      String fieldName = field.getName();
      TableColumn tableColumnName = new TableColumn(fieldName.substring(1,fieldName.length()));
      tableColumnName.setCellFactory(new PropertyValueFactory<ReserveStationTableRow, String>(fieldName));
      tableColumnsList.add(tableColumnName);
    }

    table.getColumns().addAll(tableColumnsList);
    final VBox vbox = new VBox();
    vbox.setSpacing(5);
    vbox.setPadding(new Insets(10, 0, 0, 10));
    vbox.getChildren().addAll(label, table);

    ((Group) scene.getRoot()).getChildren().addAll(vbox);

    stage.setScene(scene);
    stage.show();
  }

}

