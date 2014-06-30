package com.abcdel.tomasulo.simulator;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkState;

public class TomasuloCpu {

    public static final int NO_REGISTER = -1;
    public static final int NO_RESERVE_STATION = -1;

    public int[] registers;
    public RegisterStat[] registerStatus;
    public Map<FunctionalUnit.Type, FunctionalUnit[]> functionalUnits = new HashMap<>();
    public Map<FunctionalUnit.Type, ReserveStation[]> reserveStations = new HashMap<>();
    public AtomicInteger programCounter = new AtomicInteger(0);
    public Queue<Integer> loadStoreQueue = new LinkedList<>();

    /* TODO: Privatize */
    public TomasuloCpu(int numberOfRegisters, Map<FunctionalUnit.Type, int[]> functionalUnitsCount) {
        registers = new int[numberOfRegisters];
        // Uncomment for debugging initial state
        //for (int i = 0; i < registers.length; i++) {
        //    registers[i] = i; /* TODO: Remove */
        //}
        registerStatus = new RegisterStat[numberOfRegisters];
        for (int i = 0; i < registerStatus.length; i++) {
            registerStatus[i] = new RegisterStat();
        }
        for (Map.Entry<FunctionalUnit.Type, int[]> entry : functionalUnitsCount.entrySet()) {
            FunctionalUnit.Type type = entry.getKey();
            int[] params = entry.getValue();
            checkState(params.length == 2, "Functional units parameter is malformed");
            int numberOfFunctionalUnits = params[0];
            int numberOfReserveStations = params[1];
            FunctionalUnit[] fus = new FunctionalUnit[numberOfFunctionalUnits];
            for (int i = 0; i < fus.length; i++) {
                fus[i] = new FunctionalUnit();
            }
            functionalUnits.put(type, fus);
            ReserveStation[] rss = new ReserveStation[numberOfReserveStations];
            for (int i = 0; i < rss.length; i++) {
                rss[i] = new ReserveStation(i + 1, type);
            }
            reserveStations.put(type, rss);
        }
    }

    /* TODO: Make builder */
}
