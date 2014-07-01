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
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.abcdel.tomasulo.ui.application.MainApplication.ApplicationState;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class UiSimulator implements ApplicationToolbarHandler.ApplicationToolbarListener {

    private static final String CONFIG_FILE_NAME = "tomasulo.config";
    private static final ExecutorService POOL = Executors.newSingleThreadExecutor();
    private static final int INSTRUCTION_SIZE = 32; // In bits

    private final MainApplication mApplication;
    private final Thread mFxThread;
    private Simulator mSimulator;
    private TomasuloCpu mCpu;
    private RecentsTrackerMemoryDecorator mMemory;
    private List<Instruction> mProgram;
    private int mInterval;

    public UiSimulator(MainApplication application) {
        application.addToolbarListener(this);
        mApplication = application;
        checkState(Platform.isFxApplicationThread());
        mFxThread = Thread.currentThread();
        POOL.submit(mPlay);
        POOL.shutdown();
    }

    private File getConfigurationFile() {
        try {
            String prePath = UiSimulator.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            return new File(prePath).toPath().resolveSibling(CONFIG_FILE_NAME).toFile();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public void setup() {
        File file = getConfigurationFile();
         Configuration config;
        if (file != null && file.isFile()) {
            System.out.print("Configuration file exists, trying to read from it... ");
            try {
                System.out.println("SUCCESS");
                config = Configuration.fromFile(file);
            } catch (Configuration.FileMalformedException e) {
                System.out.println("File malformed, falling back to default settings");
                config = Configuration.defaults();
            }
        } else {
            System.out.println("No configuration file, using defaults");
            config = Configuration.defaults();
        }
        TomasuloCpu.Builder builder = new TomasuloCpu.Builder();
        builder.setNumberOfRegisters(config.registers);
        for (Map.Entry<FunctionalUnit.Type, Configuration.FunctionalUnitSpecs>
                entry : config.functionalUnits.entrySet()) {
            FunctionalUnit.Type type = entry.getKey();
            Configuration.FunctionalUnitSpecs specs = entry.getValue();
            builder.setFunctionalUnits(type, specs.functionalUnits, specs.reserveStations);
        }
        mCpu = builder.build();
        Memory memory;
        if (config.useCache) {
            memory = new TwoLevelCachedMemoryAdapter(new TwoLevelCachedMemory(
                    config.l1.size,
                    config.l1.blocksPerSet,
                    config.l2.size,
                    config.l2.blocksPerSet));
        } else {
            memory = new ConstantUniformAccessTimeMemory(config.constantMemoryAccessTime);
        }
        mMemory = new RecentsTrackerMemoryDecorator(memory, config.track);
        mInterval = config.interval;
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
            boolean textBinary = isBinary(instructions.get(0));
            mProgram = (textBinary)
                       ? Conversion.toReadableInstruction(instructions)
                       : Conversion.fromLiteralToReadableInstruction(instructions);
            mSimulator = newSimulator();
            mApplication.setApplicationState(ApplicationState.LOADED);
        } catch (IOException e) {
            e.printStackTrace();
            /* TODO: Print pretty message */
        }
    }

    private boolean isBinary(String s) {
        s = s.replaceFirst(";(.*)$", "");
        return s.replaceAll("[^10]", "").length() >= INSTRUCTION_SIZE;
    }

    private AtomicBoolean mPlaying = new AtomicBoolean(false);

    private final Runnable mPlay = new Runnable() {
        @Override
        public void run() {
            while (mFxThread.isAlive()) {
                try {
                    Thread.sleep(mInterval);
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
