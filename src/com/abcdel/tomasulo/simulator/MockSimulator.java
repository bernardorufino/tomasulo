package com.abcdel.tomasulo.simulator;

import com.abcdel.tomasulo.simulator.instruction.Add;
import com.abcdel.tomasulo.ui.MainApplication;
import com.abcdel.tomasulo.ui.MainApplication.*;
import javafx.application.Platform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MockSimulator implements ApplicationListener, Simulator {

    private MainApplication mApplication;
    private List<ReserveStation> mReserveStations;

    public MockSimulator(MainApplication application) {
        mApplication = application;
        mReserveStations = new ArrayList<ReserveStation>();
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
                    ReserveStation rs = new ReserveStation();
                    rs.id = "R1";
                    rs.type = "Add";
                    rs.busy = true;
                    rs.A = 0;
                    rs.instruction = new Add(0,0,0);
                    rs.Qj = new ReserveStation();
                    rs.Qk = new ReserveStation();
                    rs.state = ReserveStation.State.EXECUTE;
                    rs.Vj = (new Random()).nextInt();
                    rs.Vk = 2;
                    mReserveStations.clear();
                    mReserveStations.add(rs);
                    mApplication.bind(mReserveStations);
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
