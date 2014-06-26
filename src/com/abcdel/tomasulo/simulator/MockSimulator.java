package com.abcdel.tomasulo.simulator;

import com.abcdel.tomasulo.ui.MainApplication;
import com.abcdel.tomasulo.ui.MainApplication.*;
import javafx.application.Platform;

import java.io.File;

public class MockSimulator implements ApplicationListener, Simulator {

    private MainApplication mApplication;

    public MockSimulator(MainApplication application) {
        mApplication = application;
        application.addListener(this);
    }

    @Override
    public void onFileLoaded(File file) {
        System.out.println("onFileLoaded - File loaded: " + file.getPath());
        mApplication.setApplicationState(ApplicationState.LOADED);
    }

    @Override
    public void onPlay() {
        System.out.println("onPlay");
        mApplication.setApplicationState(ApplicationState.RUNNING);
    }

    @Override
    public void onStop() {
        System.out.println("onStop");
        mApplication.setApplicationState(ApplicationState.LOADED);
    }

    @Override
    public void onStep() {
        System.out.println("onStep");
        mApplication.setApplicationState(ApplicationState.STEPPING);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        mApplication.setApplicationState(ApplicationState.PAUSED);
                    }
                });
            }
        }).start();

    }

    @Override
    public void onPause() {
        System.out.println("onPause");
        mApplication.setApplicationState(ApplicationState.PAUSED);
    }
}
