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

public class ApplicationToolbarHandler implements ApplicationHandler {

    private static final int BUTTON_IMAGE_DIMENSION = 15;

    private Button mPlayButton;
    private Button mStopButton;
    private Button mStepButton;
    private Button mFileButton;
    private ImageView mPlayIcon;
    private ImageView mPauseIcon;
    private Label mLoadedFileLabel;
    private Label mStateLabel;

    private final List<ApplicationToolbarListener> mListeners = new ArrayList<ApplicationToolbarListener>();
    private MainApplication mApplication;

    public ApplicationToolbarHandler(MainApplication application) {
        mApplication = application;
    }

    @Override
    public Node createPane() {
        ToolBar toolBar = new ToolBar();

        Region spacerLeft = new Region();
        spacerLeft.getStyleClass().setAll("spacer");
        Region spacerCenter = new Region();
        spacerCenter.getStyleClass().setAll("spacer");
        Region spacerRight = new Region();
        spacerRight.getStyleClass().setAll("spacer");

        HBox buttonBar = new HBox();
        buttonBar.getStyleClass().setAll("segmented-button-bar");
        createButtons();
        buttonBar.getChildren().addAll(mPlayButton, mStopButton, mStepButton, mFileButton);
        setButtonListeners();

        mLoadedFileLabel = new Label("No File was loaded");
        mLoadedFileLabel.setTextFill(Color.BLACK);
        mStateLabel = new Label();
        mStateLabel.setTextFill(Color.BLACK);

        toolBar.getItems().addAll(spacerLeft, buttonBar, spacerCenter, mStateLabel, spacerRight, mLoadedFileLabel);
        return toolBar;
    }

    @Override
    public void addListener(MainApplication.ApplicationListener listener) {
        if (!(listener instanceof  ApplicationToolbarListener)) {
            return;
        }
        ApplicationToolbarListener toolbarListener = (ApplicationToolbarListener) listener;
        mListeners.add(toolbarListener);
    }

    @Override
    public void bind(MainApplication.ApplicationData data) {
        // No need to bind data
    }

    @Override
    public void updateApplicationState(MainApplication.ApplicationState applicationState){
        updateEnabledButtons(applicationState);
        updatePlayIcon(applicationState);
        updateStateLabel(applicationState);
    }

    private void updateStateLabel(MainApplication.ApplicationState applicationState) {
        mStateLabel.setText(String.format("%10s", applicationState.toString()));
    }

    private void createButtons() {
        mPlayButton = new Button();
        mPlayIcon = fetchIcon("play.png");
        mPauseIcon = fetchIcon("pause.png");
        updatePlayIcon(mApplication.getApplicationState());
        mPlayButton.getStyleClass().addAll("first");

        mStopButton = new Button();
        setButtonIcon(mStopButton, "stop.png");

        mStepButton = new Button();
        setButtonIcon(mStepButton, "step.png");

        mFileButton = new Button();
        setButtonIcon(mFileButton, "folder.png");
        mFileButton.getStyleClass().addAll("last", "capsule");
    }

    private void setButtonIcon(Button button, String iconName) {
        ImageView icon = fetchIcon(iconName);
        if (icon != null) {
            button.setGraphic(icon);
        }
    }

    private ImageView fetchIcon(String iconName) {
        ImageView icon = null;
        try {
            icon = new ImageView(new Image(
                    new FileInputStream("res/icons/" + iconName),
                    BUTTON_IMAGE_DIMENSION,
                    BUTTON_IMAGE_DIMENSION,
                    true,
                    true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return icon;
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
            case IDLE:
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
                mFileButton.setDisable(false);
                break;
            case STEPPING:
                mPlayButton.setDisable(true);
                mStepButton.setDisable(true);
                mStopButton.setDisable(false);
                mFileButton.setDisable(true);
                break;
            case FINISHED:
                mPlayButton.setDisable(false);
                mStepButton.setDisable(false);
                mStopButton.setDisable(true);
                mFileButton.setDisable(false);
                break;
            default:
                break;
        }
    }


    public interface ApplicationToolbarListener extends MainApplication.ApplicationListener {
        public void onFileLoaded(File file);

        public void onPlay();

        public void onStop();

        public void onStep();

        public void onPause();
    }

}
