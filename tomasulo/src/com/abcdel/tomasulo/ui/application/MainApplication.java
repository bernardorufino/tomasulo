package com.abcdel.tomasulo.ui.application;

import com.abcdel.tomasulo.simulator.RegisterStatus;
import com.abcdel.tomasulo.simulator.ReserveStation;
import com.abcdel.tomasulo.simulator.UiSimulator;
import com.abcdel.tomasulo.ui.application.handlers.ApplicationContentHandler;
import com.abcdel.tomasulo.ui.application.handlers.ApplicationHandler;
import com.abcdel.tomasulo.ui.application.handlers.ApplicationToolbarHandler;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Map;

public class MainApplication extends Application {

    private Stage mMainStage;
    private ApplicationHandler mApplicationToolbarHandler;
    private ApplicationHandler mApplicationContentHandler;

    private ApplicationState mApplicationState;
    private UiSimulator mUiSimulator;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        mMainStage = stage;

        mApplicationToolbarHandler = new ApplicationToolbarHandler(this);
        mApplicationContentHandler = new ApplicationContentHandler();

        mUiSimulator = new UiSimulator(this);

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

    public void setOnCloseRequest(EventHandler<WindowEvent> handler) {
        mMainStage.setOnCloseRequest(handler);
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
        mApplicationContentHandler.updateApplicationState(mApplicationState);
        mApplicationToolbarHandler.updateApplicationState(mApplicationState);
    }

    public void bind(ApplicationData data) {
        mApplicationContentHandler.bind(data);
        mApplicationToolbarHandler.bind(data);
    }

    private Node createContentPane() {
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(mApplicationToolbarHandler.createPane());

        borderPane.setCenter(mApplicationContentHandler.createPane());

        setApplicationState(ApplicationState.IDLE);

        return borderPane;
    }

    public enum ApplicationState {
        IDLE("IDLE"), LOADED("LOADED"), RUNNING("RUNNING"), PAUSED("PAUSED"), STEPPING("RUNNING"), FINISHED("FINISHED");

        private String mName;

        private ApplicationState(String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    public static interface ApplicationListener {
    }

    public static class ApplicationData {
        public ReserveStation[] reserveStations;
        public RegisterStatus[] registerStats;
        public int clock;
        public String PC;
        public int concludedInstructionCount;
        public double CPI;
        public Map<Integer, Integer> recentlyUsedMemory;
    }
}

