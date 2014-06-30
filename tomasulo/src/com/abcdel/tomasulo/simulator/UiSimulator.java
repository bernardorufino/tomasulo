package com.abcdel.tomasulo.simulator;

import com.abcdel.tomasulo.simulator.helper.Conversion;
import com.abcdel.tomasulo.simulator.instruction.Instruction;
import com.abcdel.tomasulo.simulator.instruction.Instructions;
import com.abcdel.tomasulo.simulator.memory.ConstantUniformAccessTimeMemory;
import com.abcdel.tomasulo.simulator.memory.Memory;
import com.abcdel.tomasulo.simulator.memory.RecentsTrackerMemoryDecorator;
import com.abcdel.tomasulo.simulator.memory.TwoLevelCachedMemoryAdapter;
import com.abcdel.tomasulo.ui.application.MainApplication;
import com.abcdel.tomasulo.ui.application.handlers.ApplicationToolbarHandler;
import com.google.common.collect.Iterables;
import com.marula.cache.memory.TwoLevelCachedMemory;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.abcdel.tomasulo.ui.application.MainApplication.ApplicationState;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class UiSimulator implements ApplicationToolbarHandler.ApplicationToolbarListener {

    private static final int INTERVAL = 7;
    private static final int MEMORY_RECENTS_TO_TRACK = 10;
    private static final ExecutorService POOL = Executors.newSingleThreadExecutor();
    private static final boolean READ_FROM_TEXT_BINARY = true; // false reads from text string
    private static final boolean TWO_LEVEL_CACHED_MEMORY = false; // false for constant access time memory


    private static class TwoLevelCachedMemoryParams {

        private static final int L1_SIZE = 64 * 1024 / 4;
        private static final int L1_BLOCKS_PER_SET = 2;
        private static final int L2_SIZE = 1 * 1024 * 1024 / 4;
        private static final int L2_BLOCKS_PER_SET = 16;
    }

    private final MainApplication mApplication;
    private Simulator mSimulator;
    private TomasuloCpu mCpu;
    private RecentsTrackerMemoryDecorator mMemory;
    private List<Instruction> mProgram;
    private final Thread mFxThread;

    public UiSimulator(MainApplication application) {
        application.addToolbarListener(this);
        mApplication = application;
        checkState(Platform.isFxApplicationThread());
        mFxThread = Thread.currentThread();
        POOL.submit(mPlay);
        POOL.shutdown();
    }

    public void setup() {
        mCpu = new TomasuloCpu.Builder()
                .setNumberOfRegisters(32)
                .setFunctionalUnits(FunctionalUnit.Type.ADD, 4, 4)
                .setFunctionalUnits(FunctionalUnit.Type.MULT, 2, 2)
                .setFunctionalUnits(FunctionalUnit.Type.LOAD, 2, 2)
                .setFunctionalUnits(FunctionalUnit.Type.BRANCH, 1, 1)
                .build();
        Memory memory;
        if (TWO_LEVEL_CACHED_MEMORY) {
            memory = new TwoLevelCachedMemoryAdapter(new TwoLevelCachedMemory(
                    TwoLevelCachedMemoryParams.L1_SIZE,
                    TwoLevelCachedMemoryParams.L1_BLOCKS_PER_SET,
                    TwoLevelCachedMemoryParams.L2_SIZE,
                    TwoLevelCachedMemoryParams.L2_BLOCKS_PER_SET));
        } else {
            memory = new ConstantUniformAccessTimeMemory();
        }
        mMemory = new RecentsTrackerMemoryDecorator(memory, MEMORY_RECENTS_TO_TRACK);
    }

    private Simulator newSimulator() {
        setup();
        return new Simulator(checkNotNull(mCpu), checkNotNull(mMemory), checkNotNull(mProgram));
    }

    @Override
    public void onFileLoaded(File file) {
        try {
            onStop();
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

    private final Runnable mPlay = new Runnable() {
        @Override
        public void run() {
            while (mFxThread.isAlive()) {
                try {
                    Thread.sleep(INTERVAL);
                } catch (InterruptedException e) {
                    AssertionError error = new AssertionError();
                    error.initCause(e);
                    throw error;
                }
                if (mPlaying.get()) {
                    runSimulationStep();
                }
            }
        }
    };

    @Override
    public void onPlay() {
        mPlaying.set(true);
        mApplication.setApplicationState(ApplicationState.RUNNING);
    }

    @Override
    public void onStop() {
        mPlaying.set(false);
        mApplication.setApplicationState(ApplicationState.LOADED);
    }

    @Override
    public void onStep() {
        mApplication.setApplicationState(ApplicationState.STEPPING);
        runSimulationStep();
        mApplication.setApplicationState(ApplicationState.PAUSED);
    }

    @Override
    public void onPause() {
        mPlaying.set(false);
        mApplication.setApplicationState(ApplicationState.PAUSED);
    }

    @Override
    public void onRestart() {
        mSimulator = newSimulator();
        mApplication.setApplicationState(ApplicationState.LOADED);
    }

    private void runSimulationStep() {
        if (mSimulator.hasFinished()) {
            mPlaying.set(false);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    mApplication.setApplicationState(ApplicationState.FINISHED);
                }
            });
            return;
        }
        mSimulator.clock();
        bindSimulator();
    }

    private void bindSimulator() {
        List<ReserveStation> reserveStations = new ArrayList<>();
        Iterables.addAll(reserveStations, mCpu.allReserveStations());
        ReserveStation[] rsArray = reserveStations.toArray(new ReserveStation[reserveStations.size()]);
        for (int i = 0; i < mCpu.registerStatus.length; i++) {
            mCpu.registerStatus[i].Vi = mCpu.registers[i];
        }

        final MainApplication.ApplicationData applicationData = new MainApplication.ApplicationData();
        applicationData.reserveStations = rsArray;
        applicationData.registerStats = mCpu.registerStatus;
        applicationData.clock = mSimulator.getClock();
        int count = mSimulator.getInstructionsFinished();
        applicationData.concludedInstructionCount = count;
        applicationData.CPI = (count == 0) ? 0 : (double) applicationData.clock / count;
        applicationData.PC = mCpu.programCounter.get() + ": " + Instructions.toString(mSimulator.getCurrentInstruction());
        applicationData.recentlyUsedMemory = mMemory.getRecentMemory();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mApplication.bind(applicationData);
            }
        });
    }
}
