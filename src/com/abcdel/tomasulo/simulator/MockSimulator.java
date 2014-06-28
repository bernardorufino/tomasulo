package com.abcdel.tomasulo.simulator;

import com.abcdel.tomasulo.simulator.instruction.Add;
import com.abcdel.tomasulo.ui.application.MainApplication;
import com.abcdel.tomasulo.ui.application.MainApplication.*;
import com.abcdel.tomasulo.ui.application.handlers.ApplicationToolbarHandler;
import javafx.application.Platform;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MockSimulator implements ApplicationToolbarHandler.ApplicationToolbarListener, Simulator {

    private MainApplication mApplication;
    private List<ReserveStation> mReserveStations;
    private List<RegisterStat> mRegisterStats;

    public MockSimulator(MainApplication application) {
        mApplication = application;
        mReserveStations = new ArrayList<ReserveStation>();
        mRegisterStats = new ArrayList<RegisterStat>();
        for (int i = 0; i < 1; i++) {
            mReserveStations.add(new ReserveStation());
        }
        for (int i = 0; i < 1; i++) {
            mRegisterStats.add(new RegisterStat());
        }
        application.addToolbarListener(this);
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
                    Thread.sleep(500);

                    ReserveStation reserveStation = new ReserveStation();
                    reserveStation.id = "Add1";
                    reserveStation.type = "Add";
                    reserveStation.busy = true;
                    reserveStation.A = 0;
                    reserveStation.instruction = new Add(0,0,0);
                    reserveStation.Qj = new ReserveStation();
                    reserveStation.Qk = new ReserveStation();
                    reserveStation.state = ReserveStation.State.EXECUTE;
                    reserveStation.Vj = (new Random()).nextInt();
                    reserveStation.Vk = 2;

                    RegisterStat registerStat = new RegisterStat();
                    registerStat.Qi = reserveStation;
                    registerStat.Vi = reserveStation.Vj;

                    mReserveStations.set(0, reserveStation);
                    mRegisterStats.set(0, registerStat);

                    mApplication.bind(
                            mReserveStations.toArray(new ReserveStation[mReserveStations.size()]),
                            mRegisterStats.toArray(new RegisterStat[mRegisterStats.size()]));
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
