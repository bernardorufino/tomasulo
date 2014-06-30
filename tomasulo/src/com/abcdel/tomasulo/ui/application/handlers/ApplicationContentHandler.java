package com.abcdel.tomasulo.ui.application.handlers;

import com.abcdel.tomasulo.simulator.RegisterStatus;
import com.abcdel.tomasulo.simulator.ReserveStation;
import com.abcdel.tomasulo.ui.application.MainApplication;
import com.abcdel.tomasulo.ui.application.handlers.TableRowData.GeneralInformationTableRow;
import com.abcdel.tomasulo.ui.application.handlers.TableRowData.RecentlyUsedMemoryTableRow;
import com.abcdel.tomasulo.ui.application.handlers.TableRowData.RegisterTableRow;
import com.abcdel.tomasulo.ui.application.handlers.TableRowData.ReserveStationTableRow;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ApplicationContentHandler implements ApplicationHandler {

    private TableView<ReserveStationTableRow> mReserveStationTable;
    private TableView<RegisterTableRow> mRegisterTable;
    private TableView<RecentlyUsedMemoryTableRow> mRecentlyUsedMemoryTable;
    private TableView<GeneralInformationTableRow> mGeneralInformationTable;

    @Override
    public Node createPane() {
        BorderPane borderPane = new BorderPane();

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
        return borderPane;
    }

    @Override
    public void bind(MainApplication.ApplicationData data) {
        List<ReserveStationTableRow> reserveStationTableRows = new ArrayList<>();
        for (ReserveStation rs : data.reserveStations) {
            reserveStationTableRows.add((new ReserveStationTableRow.Builder()).from(rs).build());
        }
        mReserveStationTable.getItems().setAll(reserveStationTableRows);

        List<RegisterTableRow> registerStatTableRows = new ArrayList<>();
        int i = 0;
        for (RegisterStatus regStat : data.registerStats) {
            registerStatTableRows.add((new RegisterTableRow.Builder()).from(regStat).setReg(i).build());
            i++;
        }
        mRegisterTable.getItems().setAll(registerStatTableRows);

        GeneralInformationTableRow.Builder genInfoTableRowsBuilder = new GeneralInformationTableRow.Builder();
        genInfoTableRowsBuilder.from(data);
        mGeneralInformationTable.getItems().setAll(genInfoTableRowsBuilder.buildAll());

        RecentlyUsedMemoryTableRow.Builder recentlyUsedMemoryRowsBuilder = new RecentlyUsedMemoryTableRow.Builder();
        recentlyUsedMemoryRowsBuilder.from(data.recentlyUsedMemory);
        mRecentlyUsedMemoryTable.getItems().setAll(recentlyUsedMemoryRowsBuilder.buildAll());
    }

    @Override
    public void updateApplicationState(MainApplication.ApplicationState applicationState) {
        if (applicationState == MainApplication.ApplicationState.LOADED) {
            mReserveStationTable.getItems().clear();
            mRecentlyUsedMemoryTable.getItems().clear();
            mGeneralInformationTable.getItems().clear();
            mRegisterTable.getItems().clear();
        }
    }

    @Override
    public void addListener(MainApplication.ApplicationListener listener) {
        // No listener are defined
    }

    private Node createSecondaryTables() {
        final HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.getChildren().add(createRecentlyUsedMemoryTable());
        hbox.getChildren().add(createGeneralInformationTable());
        return hbox;
    }

    private Node createRegistersTable() {
        mRegisterTable = new TableView<>();
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
        mGeneralInformationTable = new TableView<>();
        Field[] fields = GeneralInformationTableRow.class.getDeclaredFields();
        for(Field field : fields) {
            String fieldName = field.getName();
            TableColumn<GeneralInformationTableRow, String> column = new TableColumn<GeneralInformationTableRow, String>(fieldName.substring(1, fieldName.length()));
            column.setCellValueFactory(new PropertyValueFactory<GeneralInformationTableRow, String>(fieldName));
            column.prefWidthProperty().bind(mGeneralInformationTable.widthProperty().divide(fields.length));
            mGeneralInformationTable.getColumns().add(column);
        }
        return configureTable(mGeneralInformationTable, "General Information", 500, 400);
    }

    private Node createRecentlyUsedMemoryTable() {
        mRecentlyUsedMemoryTable = new TableView<>();
        Field[] fields = RecentlyUsedMemoryTableRow.class.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            TableColumn<RecentlyUsedMemoryTableRow, String> column = new TableColumn<RecentlyUsedMemoryTableRow, String>(fieldName.substring(1, fieldName.length()));
            column.setCellValueFactory(new PropertyValueFactory<RecentlyUsedMemoryTableRow, String>(fieldName));
            column.prefWidthProperty().bind(mRecentlyUsedMemoryTable.widthProperty().divide(fields.length));
            mRecentlyUsedMemoryTable.getColumns().add(column);
        }
        return configureTable(mRecentlyUsedMemoryTable, "Recently Used Memory", 500, 400);
    }

    private Node createReserveStationTable() {
        mReserveStationTable = new TableView<>();
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
}
