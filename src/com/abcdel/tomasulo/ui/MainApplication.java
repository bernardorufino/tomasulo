package com.abcdel.tomasulo.ui;

import com.abcdel.tomasulo.simulator.ReserveStation;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.Observable;

public class MainApplication extends Application {

    private static final int BUTTON_IMAGE_DIMENSION = 15;

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
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setContent(createContentPane());
        ((Group) scene.getRoot()).getChildren().addAll(sp);
        //stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    private Node createContentPane() {
        BorderPane borderPane = new BorderPane();

        ToolBar toolBar = new ToolBar();
        borderPane.setTop(toolBar);

        Region spacer = new Region();
        spacer.getStyleClass().setAll("spacer");

        HBox buttonBar = new HBox();
        buttonBar.getStyleClass().setAll("segmented-button-bar");

        Button playButton = new Button();
        try {
            playButton.setGraphic(new ImageView(new Image(
                    new FileInputStream("res/icons/play.png"),
                    BUTTON_IMAGE_DIMENSION,
                    BUTTON_IMAGE_DIMENSION,
                    true,
                    true)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        playButton.getStyleClass().addAll("first");

        Button stopButton = new Button();
        try {
            stopButton.setGraphic(new ImageView(new Image(
                    new FileInputStream("res/icons/stop.png"),
                    BUTTON_IMAGE_DIMENSION,
                    BUTTON_IMAGE_DIMENSION,
                    true,
                    true)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Button stepButton = new Button();
        try {
            stepButton.setGraphic(new ImageView(new Image(
                    new FileInputStream("res/icons/step.png"),
                    BUTTON_IMAGE_DIMENSION,
                    BUTTON_IMAGE_DIMENSION,
                    true,
                    true)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Button openButton = new Button();
        try {
            openButton.setGraphic(new ImageView(new Image(
                    new FileInputStream("res/icons/folder.png"),
                    BUTTON_IMAGE_DIMENSION,
                    BUTTON_IMAGE_DIMENSION,
                    true,
                    true)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        openButton.getStyleClass().addAll("last", "capsule");

        buttonBar.getChildren().addAll(playButton, stopButton, stepButton, openButton);
        toolBar.getItems().addAll(spacer, buttonBar);

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setSpacing(10);

        vbox.getChildren().add(createReserveStationTable());
        vbox.getChildren().add(createSecondaryTables());

        borderPane.setCenter(vbox);
        return borderPane;
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
        TableView<ReserveStationTableRow> table = new TableView<ReserveStationTableRow>();
        table.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        table.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        table.setPrefWidth(1000);
        table.setEditable(false);
        Field[] fields = ReserveStationTableRow.class.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            TableColumn<ReserveStationTableRow, String> column = new TableColumn<ReserveStationTableRow, String>(fieldName.substring(1, fieldName.length()));
            column.setCellValueFactory( new PropertyValueFactory<ReserveStationTableRow, String>(fieldName));
            column.prefWidthProperty().bind(table.widthProperty().divide(fields.length));
            table.getColumns().add(column);
        }

        ObservableList<ReserveStationTableRow> testList =
                FXCollections.observableArrayList(
                        new ReserveStationTableRow("R1", "Add", "yes", "Execute", "", "Mem[34 + Regs[R2]", "Load2", "", ""));

        table.setItems(testList);
        vbox.setSpacing(5);
        vbox.getChildren().addAll(label, table);
        return vbox;
    }
}

