package com.abcdel.tomasulo.simulator;

import com.abcdel.tomasulo.simulator.instruction.*;
import com.abcdel.tomasulo.simulator.memory.ConstantUniformAccessTimeMemory;
import com.abcdel.tomasulo.simulator.memory.Memory;
import com.abcdel.tomasulo.simulator.memory.TwoLevelCachedMemoryDecorator;
import com.abcdel.tomasulo.ui.application.MainApplication;
import com.abcdel.tomasulo.ui.application.handlers.ApplicationToolbarHandler;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.abcdel.tomasulo.ui.application.MainApplication.ApplicationState;

public class UiSimulator implements ApplicationToolbarHandler.ApplicationToolbarListener {

    private final MainApplication mApplication;
    private final Simulator mSimulator;
    private TomasuloCpu mCpu;
    private Memory mMemory;

    public UiSimulator(MainApplication application) {
        application.addToolbarListener(this);
        mApplication = application;
        mSimulator = newSimulator();
    }

    public Simulator newSimulator() {
        mCpu = new TomasuloCpu.Builder()
                .setNumberOfRegisters(32)
                .setFunctionalUnits(FunctionalUnit.Type.ADD, 3, 3)
                .setFunctionalUnits(FunctionalUnit.Type.MULT, 2, 2)
                .setFunctionalUnits(FunctionalUnit.Type.LOAD, 2, 2)
                .build();
        mMemory = new ConstantUniformAccessTimeMemory();
        List<Instruction> instructions = ImmutableList.of(
                new Lw(6, 2, 34),
                new Lw(2, 3, 45),
                new Mul(0, 2, 4),
                new Sub(8, 6, 2),
                new Mul(10, 0, 6),
                new Add(6, 8, 2)
        );
        return new Simulator(mCpu, mMemory, instructions);
    }

    @Override
    public void onFileLoaded(File file) {
        System.out.println("onFileLoaded - File loaded: " + file.getPath());
        mApplication.setApplicationState(ApplicationState.LOADED);
    }

    @Override
    public void onPlay() {
        mApplication.setApplicationState(ApplicationState.LOADED);
        System.out.println("onPlay");
    }

    @Override
    public void onStop() {
        System.out.println("onStop");
    }

    @Override
    public void onStep() {
        mApplication.setApplicationState(ApplicationState.STEPPING);
        mSimulator.clock();
        System.out.println(mSimulator.getClock());
        bindSimulator();
        mApplication.setApplicationState(ApplicationState.PAUSED);
    }

    private void bindSimulator() {
        /* TODO: Change register status */
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
