package com.abcdel.tomasulo.simulator;

import com.abcdel.tomasulo.ui.application.MainApplication;
import com.abcdel.tomasulo.ui.application.MainApplication.ApplicationState;
import com.abcdel.tomasulo.ui.application.handlers.ApplicationToolbarHandler;
import javafx.application.Platform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MockUiSimulator implements ApplicationToolbarHandler.ApplicationToolbarListener {

    private MainApplication mApplication;
    private List<ReserveStation> mReserveStations;
    private List<RegisterStatus> mRegisterStats;

    private boolean mBreakSimulation;
    private ApplicationState mNextState;
    private Thread mExecutionThread;

    public MockUiSimulator(MainApplication application) {
        application.addToolbarListener(this);

        mApplication = application;

        mReserveStations = new ArrayList<ReserveStation>();
        mRegisterStats = new ArrayList<RegisterStatus>();
        for (int i = 0; i < 1; i++) {
            mReserveStations.add(new ReserveStation(i, null));
        }
        for (int i = 0; i < 1; i++) {
            mRegisterStats.add(new RegisterStatus());
        }
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
        mBreakSimulation = false;
        startExecution();
    }

    @Override
    public void onStop() {
        System.out.println("onStop");
        mNextState = ApplicationState.LOADED;
        mBreakSimulation = true;
        if (!mExecutionThread.isAlive()) {
            mApplication.setApplicationState(mNextState);
        }
    }

    @Override
    public void onStep() {
        System.out.println("onStep");
        mApplication.setApplicationState(ApplicationState.STEPPING);
        mNextState = ApplicationState.PAUSED;
        mBreakSimulation = true;
        startExecution();
    }

    @Override
    public void onPause() {
        System.out.println("onPause");
        mBreakSimulation = true;
        mNextState = ApplicationState.PAUSED;
    }

    private void startExecution() {
        mExecutionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    do {
                        Thread.sleep(500);

                        ReserveStation reserveStation = new ReserveStation(0, null);
//                        ReserveStation reserveStation = new ReserveStation();
//                        reserveStation.id = "Add1";
//                        reserveStation.type = "Add";
//                        reserveStation.busy = true;
//                        reserveStation.A = 0;
//                        reserveStation.instruction = new Add(0, 0, 0);
//                        reserveStation.Qj = new ReserveStation();
//                        reserveStation.Qk = new ReserveStation();
//                        reserveStation.state = ReserveStation.State.EXECUTE;
//                        reserveStation.Vj = (new Random()).nextInt(256);
//                        reserveStation.Vk = 2;

                        RegisterStatus registerStat = new RegisterStatus();
                        registerStat.Qi = reserveStation;
                        registerStat.Vi = reserveStation.Vj;

                        mReserveStations.set(0, reserveStation);
                        mRegisterStats.set(0, registerStat);

                        mApplication.bind(
                                mReserveStations.toArray(new ReserveStation[mReserveStations.size()]),
                                mRegisterStats.toArray(new RegisterStatus[mRegisterStats.size()]),
                                10);
                    } while (!mBreakSimulation);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            mApplication.setApplicationState(mNextState);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        mExecutionThread.start();
    }
}
