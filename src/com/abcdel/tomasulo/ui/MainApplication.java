package com.abcdel.tomasulo.ui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.abcdel.tomasulo.simulator.MockSimulator;
import com.abcdel.tomasulo.simulator.Simulator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainApplication extends Application {

    private static final int BUTTON_IMAGE_DIMENSION = 15;

    private Stage mPrimaryStage;
    private Button mPlayButton;
    private Button mStopButton;
    private Button mStepButton;
    private Button mFileButton;
    private ImageView mPlayIcon;
    private ImageView mPauseIcon;

    private final List<ApplicationListener> mListeners = new ArrayList<ApplicationListener>();
    private ApplicationState mApplicationState;
    private Simulator mSimulator;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        mPrimaryStage = stage;
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

    public void setApplicationState(ApplicationState applicationState) {
        mApplicationState = applicationState;
        updatePlayIcon();
        updateEnabledButtons();
    }

    public void addListener(ApplicationListener listener) {
        mListeners.add(listener);
    }

    private Node createContentPane() {
        BorderPane borderPane = new BorderPane();

        ToolBar toolBar = new ToolBar();
        borderPane.setTop(toolBar);

        Region spacerLeft = new Region();
        spacerLeft.getStyleClass().setAll("spacer");
        Region spacerCenter = new Region();
        spacerCenter.getStyleClass().setAll("spacer");

        HBox buttonBar = new HBox();
        buttonBar.getStyleClass().setAll("segmented-button-bar");

        mPlayButton = new Button();
        mPlayButton.getStyleClass().addAll("first");

        mPlayIcon = null;
        mPauseIcon = null;

        try {
            mPlayIcon = new ImageView(new Image(
                    new FileInputStream("res/icons/play.png"),
                    BUTTON_IMAGE_DIMENSION,
                    BUTTON_IMAGE_DIMENSION,
                    true,
                    true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            mPauseIcon = new ImageView(new Image(
                    new FileInputStream("res/icons/pause.png"),
                    BUTTON_IMAGE_DIMENSION,
                    BUTTON_IMAGE_DIMENSION,
                    true,
                    true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        updatePlayIcon();

        mStopButton = new Button();
        try {
            mStopButton.setGraphic(new ImageView(new Image(
                    new FileInputStream("res/icons/stop.png"),
                    BUTTON_IMAGE_DIMENSION,
                    BUTTON_IMAGE_DIMENSION,
                    true,
                    true)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        mStepButton = new Button();
        try {
            mStepButton.setGraphic(new ImageView(new Image(
                    new FileInputStream("res/icons/step.png"),
                    BUTTON_IMAGE_DIMENSION,
                    BUTTON_IMAGE_DIMENSION,
                    true,
                    true)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        mFileButton = new Button();
        try {
            mFileButton.setGraphic(new ImageView(new Image(
                    new FileInputStream("res/icons/folder.png"),
                    BUTTON_IMAGE_DIMENSION,
                    BUTTON_IMAGE_DIMENSION,
                    true,
                    true)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mFileButton.getStyleClass().addAll("last", "capsule");

        buttonBar.getChildren().addAll(mPlayButton, mStopButton, mStepButton, mFileButton);

        final Label loadedFileLabel = new Label("No File was loaded");
        loadedFileLabel.setTextFill(Color.BLACK);

        toolBar.getItems().addAll(spacerLeft, buttonBar, spacerCenter, loadedFileLabel);
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

        mPlayButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (ApplicationListener listener : mListeners) {
                    if (shouldDisplayPauseButton()) {
                        listener.onPause();
                    } else {
                        listener.onPlay();
                    }
                }
            }
        });
        mStopButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (ApplicationListener listener : mListeners) {
                    listener.onStop();
                }
            }
        });
        mStepButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (ApplicationListener listener : mListeners) {
                    listener.onStep();
                }
            }
        });
        mFileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showOpenDialog(mPrimaryStage);

                if (file == null) return;
                loadedFileLabel.setText("Loaded File: " + file.getPath());
                for (ApplicationListener listener : mListeners) {
                    listener.onFileLoaded(file);
                }
            }
        });

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
        TableView<RegisteTableRow> table = new TableView<RegisteTableRow>();
        Field[] fields = RegisteTableRow.class.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            TableColumn<RegisteTableRow, String> column = new TableColumn<RegisteTableRow, String>(fieldName.substring(1, fieldName.length()));
            column.setCellValueFactory(new PropertyValueFactory<RegisteTableRow, String>(fieldName));
            column.prefWidthProperty().bind(table.widthProperty().divide(fields.length));
            table.getColumns().add(column);
        }
        return configureTable(table, "Registers", 200, 850);
    }

    private Node createGeneralInformationTable() {
        TableView<GeneralInformationTableRow> table = new TableView<GeneralInformationTableRow>();
        Field[] fields = GeneralInformationTableRow.class.getDeclaredFields();
        for(Field field : fields) {
            String fieldName = field.getName();
            TableColumn<GeneralInformationTableRow, String> column = new TableColumn<GeneralInformationTableRow, String>(fieldName.substring(1, fieldName.length()));
            column.prefWidthProperty().bind(table.widthProperty().divide(fields.length));
            table.getColumns().add(column);
        }
        return configureTable(table, "General Information", 500, 400);
    }

    private Node createRecentlyUsedMemoryTable() {
        TableView<RecentlyUsedMemoryTableRow> table = new TableView<RecentlyUsedMemoryTableRow>();
        Field[] fields = RecentlyUsedMemoryTableRow.class.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            TableColumn<RecentlyUsedMemoryTableRow, String> column = new TableColumn<RecentlyUsedMemoryTableRow, String>(fieldName.substring(1, fieldName.length()));
            column.setCellValueFactory(new PropertyValueFactory<RecentlyUsedMemoryTableRow, String>(fieldName));
            column.prefWidthProperty().bind(table.widthProperty().divide(fields.length));
            table.getColumns().add(column);
        }
        return configureTable(table, "Recently Used Memory", 500, 400);
    }

    private Node createReserveStationTable() {
        TableView<ReserveStationTableRow> table = new TableView<ReserveStationTableRow>();
        Field[] fields = ReserveStationTableRow.class.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            TableColumn<ReserveStationTableRow, String> column = new TableColumn<ReserveStationTableRow, String>(fieldName.substring(1, fieldName.length()));
            column.setCellValueFactory(new PropertyValueFactory<ReserveStationTableRow, String>(fieldName));
            column.prefWidthProperty().bind(table.widthProperty().divide(fields.length));
            table.getColumns().add(column);
        }

        ObservableList<ReserveStationTableRow> testList =
                FXCollections.observableArrayList(
                        new ReserveStationTableRow("R1", "Add", "yes", "Execute", "", "Mem[34 + Regs[R2]", "Load2", "", ""));
        table.setItems(testList);

        return configureTable(table, "Reserve Stations", 1000, 400);
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

    private void updatePlayIcon() {
        mPlayButton.setGraphic(shouldDisplayPauseButton() ? mPauseIcon : mPlayIcon);
    }

    private boolean shouldDisplayPauseButton() {
        return mApplicationState == ApplicationState.RUNNING;
    }

    private void updateEnabledButtons() {
        switch (mApplicationState) {
            case STAND_BY:
                mPlayButton.setDisable(true);
                mStepButton.setDisable(true);
                mStopButton.setDisable(true);
                mFileButton.setDisable(false);
                break;
            case LOADED:
                mPlayButton.setDisable(false);
                mStepButton.setDisable(false);
                mStopButton.setDisable(true);
                mFileButton.setDisable(false);
                break;
            case RUNNING:
                mPlayButton.setDisable(false);
                mStepButton.setDisable(true);
                mStopButton.setDisable(false);
                mFileButton.setDisable(true);
                break;
            case PAUSED:
                mPlayButton.setDisable(false);
                mStepButton.setDisable(false);
                mStopButton.setDisable(false);
                mFileButton.setDisable(true);
                break;
            case STEPPING:
                mPlayButton.setDisable(true);
                mStepButton.setDisable(true);
                mStopButton.setDisable(false);
                mFileButton.setDisable(true);
                break;
            default:
                break;
        }
    }

    public enum ApplicationState {
        STAND_BY, LOADED, RUNNING, PAUSED, STEPPING
    }

    public interface ApplicationListener {
        public void onFileLoaded(File file);

        public void onPlay();

        public void onStop();

        public void onStep();

        public void onPause();
    }
}

