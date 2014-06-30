package com.abcdel.tomasulo.simulator;

import com.abcdel.tomasulo.simulator.helper.Conversion;
import com.abcdel.tomasulo.simulator.instruction.Instruction;
import com.abcdel.tomasulo.simulator.memory.ConstantUniformAccessTimeMemory;
import com.abcdel.tomasulo.simulator.memory.Memory;
import com.abcdel.tomasulo.simulator.memory.TwoLevelCachedMemoryDecorator;
import com.abcdel.tomasulo.ui.application.MainApplication;
import com.abcdel.tomasulo.ui.application.handlers.ApplicationToolbarHandler;
import com.google.common.collect.Iterables;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.abcdel.tomasulo.ui.application.MainApplication.ApplicationState;
import static com.google.common.base.Preconditions.checkNotNull;

public class UiSimulator implements ApplicationToolbarHandler.ApplicationToolbarListener {

    private static final ExecutorService POOL = Executors.newSingleThreadExecutor();
    private static final boolean READ_FROM_TEXT_BINARY = true; // false reads from text string
    private static final int INTERVAL = 10;

    private final MainApplication mApplication;
    private Simulator mSimulator;
    private TomasuloCpu mCpu;
    private Memory mMemory;
    private List<Instruction> mProgram;

    public UiSimulator(MainApplication application) {
        application.addToolbarListener(this);
        mApplication = application;
        POOL.submit(mPlay);
    }

    public void setup() {
        mCpu = new TomasuloCpu.Builder()
                .setNumberOfRegisters(32)
                .setFunctionalUnits(FunctionalUnit.Type.ADD, 3, 3)
                .setFunctionalUnits(FunctionalUnit.Type.MULT, 2, 2)
                .setFunctionalUnits(FunctionalUnit.Type.LOAD, 2, 2)
                .setFunctionalUnits(FunctionalUnit.Type.BRANCH, 1, 1)
                .build();
        mMemory = new ConstantUniformAccessTimeMemory();
    }

    private Simulator newSimulator() {
        setup();
        return new Simulator(checkNotNull(mCpu), checkNotNull(mMemory), checkNotNull(mProgram));
    }

    @Override
    public synchronized void onFileLoaded(File file) {
        try {
            List<String> instructions = Files.readAllLines(file.toPath(), Charset.defaultCharset());
            for (int i = 0, n = instructions.size(); i < n; i++) {
                if (instructions.get(i).trim().isEmpty()) instructions.remove(i);
            }
            mProgram = (READ_FROM_TEXT_BINARY)
                    ? Conversion.toReadableInstruction(instructions)
                    : Conversion.fromLiteralToReadableInstruction(instructions);
            mSimulator = newSimulator();
            mApplication.setApplicationState(ApplicationState.LOADED);
        } catch (IOException e) {
            e.printStackTrace();
            /* TODO: Print pretty message */
        }
    }

    private AtomicBoolean mPlaying = new AtomicBoolean(false);
    private Lock mLock = new ReentrantLock();

    private final Runnable mPlay = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(INTERVAL);
                } catch (InterruptedException e) {
                    AssertionError error = new AssertionError();
                    error.initCause(e);
                    throw error;
                }
                if (mPlaying.get()) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            onStep();
                        }
                    });
                }
            }
        }
    };

    @Override
    public void onPlay() {
        mPlaying.set(true);
    }

    @Override
    public void onStop() {
        mPlaying.set(false);
    }

    @Override
    public void onStep() {
        mApplication.setApplicationState(ApplicationState.STEPPING);
        mSimulator.clock();
        bindSimulator();
        mApplication.setApplicationState(ApplicationState.PAUSED);
    }

    private void bindSimulator() {
        List<ReserveStation> reserveStations = new ArrayList<>();
        Iterables.addAll(reserveStations, mCpu.allReserveStations());
        ReserveStation[] rsArray = reserveStations.toArray(new ReserveStation[reserveStations.size()]);
        for (int i = 0; i < mCpu.registerStatus.length; i++) {
            mCpu.registerStatus[i].Vi = mCpu.registers[i];
        }

        MainApplication.ApplicationData applicationData = new MainApplication.ApplicationData();
        applicationData.reserveStations = rsArray;
        applicationData.registerStats = mCpu.registerStatus;
        applicationData.clock = mSimulator.getClock();
        applicationData.CPI = 1.03; // TODO: insert actual value
        applicationData.concludedInstructionCount = 5; // TODO: insert actual value
        applicationData.PC = String.valueOf(10000); // TODO: insert actual PC
        applicationData.recentlyUsedMemory = new HashMap<>(); // TODO: Take this from the actual memory
        applicationData.recentlyUsedMemory.put(10, 20);
        mApplication.bind(applicationData);
    }

    @Override
    public void onPause() {
        System.out.println("onPause");
    }
}
