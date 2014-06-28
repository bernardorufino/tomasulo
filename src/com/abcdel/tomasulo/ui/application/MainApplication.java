package com.abcdel.tomasulo.ui.application;

import com.abcdel.tomasulo.simulator.RegisterStat;
import com.abcdel.tomasulo.simulator.ReserveStation;
import com.abcdel.tomasulo.ui.GeneralInformationTableRow;
import com.abcdel.tomasulo.ui.RecentlyUsedMemoryTableRow;
import com.abcdel.tomasulo.ui.RegisterTableRow;
import com.abcdel.tomasulo.ui.ReserveStationTableRow;
import com.abcdel.tomasulo.ui.application.handlers.ApplicationToolbarHandler;
import javafx.application.Application;
import com.abcdel.tomasulo.simulator.MockSimulator;
import com.abcdel.tomasulo.simulator.Simulator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainApplication extends Application {

    private Stage mMainStage;
    private TableView<ReserveStationTableRow> mReserveStationTable;
    private TableView<RegisterTableRow> mRegisterTable;
    private TableView<RecentlyUsedMemoryTableRow> mRecentlyUsedMemomryTable;
    private TableView<GeneralInformationTableRow> mGeneralInformationTable;
    private ApplicationToolbarHandler mApplicationToolbarHandler;

    private ApplicationState mApplicationState;
    private Simulator mSimulator;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        mMainStage = stage;

        mApplicationToolbarHandler = new ApplicationToolbarHandler(this);

        mSimulator = new MockSimulator(this);

        Scene scene = new Scene(new Group(), 900, 600);

        ScrollPane sp = new ScrollPane();
        sp.setStyle("-fx-background: rgb(80,80,80);");
        sp.prefHeightProperty().bind(scene.heightProperty());
        sp.prefWidthProperty().bind(scene.widthProperty());
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setContent(createContentPane());

        ((Group) scene.getRoot()).getChildren().addAll(sp);
        stage.setScene(scene);

        stage.setTitle("Main Application");
        stage.show();
    }

    public ApplicationState getApplicationState() {
        return mApplicationState;
    }

    public Stage getMainStage() {
        return mMainStage;
    }

    public void addToolbarListener(ApplicationToolbarHandler.ApplicationToolbarListener applicationToolbarListener) {
        mApplicationToolbarHandler.addListener(applicationToolbarListener);
    }

    public void setApplicationState(ApplicationState applicationState) {
        mApplicationState = applicationState;
        mApplicationToolbarHandler.updateApplicationState(mApplicationState);
    }

    private Node createContentPane() {

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(mApplicationToolbarHandler.createPane());

        VBox registerBox = new VBox();
        registerBox.setPadding(new Insets(10, 10, 10, 10));
        registerBox.setSpacing(10);
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setSpacing(10);

        vbox.getChildren().add(createReserveStationTable());
        vbox.getChildren().add(createSecondaryTables());

        registerBox.getChildren().add(createRegistersTable());

        borderPane.setCenter(vbox);
        borderPane.setLeft(registerBox);


        setApplicationState(ApplicationState.STAND_BY);

        return borderPane;
    }

    private Node createSecondaryTables() {
        final HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.getChildren().add(createRecentlyUsedMemoryTable());
        hbox.getChildren().add(createGeneralInformationTable());
        return hbox;
    }

    private Node createRegistersTable() {
        mRegisterTable = new TableView<RegisterTableRow>();
        Field[] fields = RegisterTableRow.class.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            TableColumn<RegisterTableRow, String> column = new TableColumn<RegisterTableRow, String>(fieldName.substring(1, fieldName.length()));
            column.setCellValueFactory(new PropertyValueFactory<RegisterTableRow, String>(fieldName));
            column.prefWidthProperty().bind(mRegisterTable.widthProperty().divide(fields.length));
            mRegisterTable.getColumns().add(column);
        }
        return configureTable(mRegisterTable, "Registers", 200, 850);
    }

    private Node createGeneralInformationTable() {
        mGeneralInformationTable = new TableView<GeneralInformationTableRow>();
        Field[] fields = GeneralInformationTableRow.class.getDeclaredFields();
        for(Field field : fields) {
            String fieldName = field.getName();
            TableColumn<GeneralInformationTableRow, String> column = new TableColumn<GeneralInformationTableRow, String>(fieldName.substring(1, fieldName.length()));
            column.prefWidthProperty().bind(mGeneralInformationTable.widthProperty().divide(fields.length));
            mGeneralInformationTable.getColumns().add(column);
        }
        return configureTable(mGeneralInformationTable, "General Information", 500, 400);
    }

    private Node createRecentlyUsedMemoryTable() {
        mRecentlyUsedMemomryTable = new TableView<RecentlyUsedMemoryTableRow>();
        Field[] fields = RecentlyUsedMemoryTableRow.class.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            TableColumn<RecentlyUsedMemoryTableRow, String> column = new TableColumn<RecentlyUsedMemoryTableRow, String>(fieldName.substring(1, fieldName.length()));
            column.setCellValueFactory(new PropertyValueFactory<RecentlyUsedMemoryTableRow, String>(fieldName));
            column.prefWidthProperty().bind(mRecentlyUsedMemomryTable.widthProperty().divide(fields.length));
            mRecentlyUsedMemomryTable.getColumns().add(column);
        }
        return configureTable(mRecentlyUsedMemomryTable, "Recently Used Memory", 500, 400);
    }

    private Node createReserveStationTable() {
        mReserveStationTable = new TableView<ReserveStationTableRow>();
        Field[] fields = ReserveStationTableRow.class.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            TableColumn<ReserveStationTableRow, String> column = new TableColumn<ReserveStationTableRow, String>(fieldName.substring(1, fieldName.length()));
            column.setCellValueFactory(new PropertyValueFactory<ReserveStationTableRow, String>(fieldName));
            column.prefWidthProperty().bind(mReserveStationTable.widthProperty().divide(fields.length));
            mReserveStationTable.getColumns().add(column);
        }
        return configureTable(mReserveStationTable, "Reserve Stations", 1000, 400);
    }

    private Node configureTable(TableView table, String name, double width, double height) {
        VBox vbox = new VBox();

        Label label = new Label(name);
        label.setFont(new Font("Arial", 20));

        table.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        table.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        table.setPrefSize(width, height);
        table.setEditable(false);
        table.getStylesheets().addAll(this.getClass().getResource("table_style.css").toExternalForm());

        vbox.setSpacing(5);
        vbox.getChildren().addAll(label, table);

        return vbox;
    }


    public void bind(ReserveStation[] reserveStations, RegisterStat[] registerStats) {
        List<ReserveStationTableRow> reserveStationTableRows = new ArrayList<ReserveStationTableRow>();
        for (ReserveStation rs : reserveStations) {
            reserveStationTableRows.add((new ReserveStationTableRow.Builder()).from(rs).build());
        }
        mReserveStationTable.getItems().setAll(reserveStationTableRows);

        List<RegisterTableRow> registerStatTableRows = new ArrayList<RegisterTableRow>();
        int i = 0;
        for (RegisterStat regStat : registerStats) {
            registerStatTableRows.add((new RegisterTableRow.Builder()).from(regStat).setReg(i).build());
            i++;
        }
        mRegisterTable.getItems().setAll(registerStatTableRows);
    }

    public enum ApplicationState {
        STAND_BY, LOADED, RUNNING, PAUSED, STEPPING
    }

}

