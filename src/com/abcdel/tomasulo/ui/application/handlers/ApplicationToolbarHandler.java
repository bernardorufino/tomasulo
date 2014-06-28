package com.abcdel.tomasulo.ui.application.handlers;

import com.abcdel.tomasulo.ui.application.MainApplication;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ApplicationToolbarHandler {

    private static final int BUTTON_IMAGE_DIMENSION = 15;

    private Button mPlayButton;
    private Button mStopButton;
    private Button mStepButton;
    private Button mFileButton;
    private ImageView mPlayIcon;
    private ImageView mPauseIcon;
    private Label mLoadedFileLabel;

    private final List<ApplicationToolbarListener> mListeners = new ArrayList<ApplicationToolbarListener>();
    private MainApplication mApplication;

    public ApplicationToolbarHandler(MainApplication application) {
        mApplication = application;
    }

    public void addListener(ApplicationToolbarListener listener) {
        mListeners.add(listener);
    }

    public void updateApplicationState(MainApplication.ApplicationState applicationState){
        updateEnabledButtons(applicationState);
        updatePlayIcon(applicationState);
    }

    public Node createPane() {
        ToolBar toolBar = new ToolBar();

        Region spacerLeft = new Region();
        spacerLeft.getStyleClass().setAll("spacer");
        Region spacerCenter = new Region();
        spacerCenter.getStyleClass().setAll("spacer");

        HBox buttonBar = new HBox();
        buttonBar.getStyleClass().setAll("segmented-button-bar");
        createButtons();
        buttonBar.getChildren().addAll(mPlayButton, mStopButton, mStepButton, mFileButton);

        mLoadedFileLabel = new Label("No File was loaded");
        mLoadedFileLabel.setTextFill(Color.BLACK);
        setButtonListeners();
        toolBar.getItems().addAll(spacerLeft, buttonBar, spacerCenter, mLoadedFileLabel);
        return toolBar;
    }

    private void createButtons() {
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

        updatePlayIcon(mApplication.getApplicationState());

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
    }

    private void setButtonListeners() {
        mPlayButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (ApplicationToolbarListener listener : mListeners) {
                    if (shouldDisplayPauseButton(mApplication.getApplicationState())) {
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
                for (ApplicationToolbarListener listener : mListeners) {
                    listener.onStop();
                }
            }
        });
        mStepButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (ApplicationToolbarListener listener : mListeners) {
                    listener.onStep();
                }
            }
        });
        mFileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showOpenDialog(mApplication.getMainStage());

                if (file == null) return;
                mLoadedFileLabel.setText("Loaded File: " + file.getPath());
                for (ApplicationToolbarListener listener : mListeners) {
                    listener.onFileLoaded(file);
                }
            }
        });
    }

    private void updatePlayIcon(MainApplication.ApplicationState applicationState) {
        mPlayButton.setGraphic(shouldDisplayPauseButton(applicationState) ? mPauseIcon : mPlayIcon);
    }

    private boolean shouldDisplayPauseButton(MainApplication.ApplicationState applicationState) {
        return applicationState == MainApplication.ApplicationState.RUNNING;
    }

    private void updateEnabledButtons(MainApplication.ApplicationState applicationState) {
        switch (applicationState) {
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


    public interface ApplicationToolbarListener {
        public void onFileLoaded(File file);

        public void onPlay();

        public void onStop();

        public void onStep();

        public void onPause();
    }

}
