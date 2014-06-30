package com.abcdel.tomasulo.simulator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public class TomasuloCpu {

    public static final int NO_REGISTER = -1;
    public static final int NO_RESERVE_STATION = -1;

    public int[] registers;
    public RegisterStatus[] registerStatus;
    public Map<FunctionalUnit.Type, FunctionalUnit[]> functionalUnits = new LinkedHashMap<>();
    public Map<FunctionalUnit.Type, ReserveStation[]> reserveStations = new LinkedHashMap<>();
    public AtomicInteger programCounter = new AtomicInteger(0);
    public Queue<Integer> loadStoreQueue = new LinkedList<>();

    private TomasuloCpu(int numberOfRegisters, Map<FunctionalUnit.Type, int[]> functionalUnitsCount) {
        checkArgument(numberOfRegisters > 0, "Number of register must be greater than zero");

        registers = new int[numberOfRegisters];
        // Uncomment for debugging initial state
        for (int i = 0; i < registers.length; i++) {
            registers[i] = i; /* TODO: Remove */
        }
        registerStatus = new RegisterStatus[numberOfRegisters];
        for (int i = 0; i < registerStatus.length; i++) {
            registerStatus[i] = new RegisterStatus();
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

    public Iterable<ReserveStation> allReserveStations() {
        Iterable<ReserveStation> ans = ImmutableList.of();
        for (ReserveStation[] rs : reserveStations.values()) {
            Iterable<ReserveStation> i = Arrays.asList(rs);
            ans = (ans == null) ? i : Iterables.concat(ans, i);
        }
        return ans;
    }

    public static class Builder {

        private int mNumberOfRegisters = 0;
        private Map<FunctionalUnit.Type, int[]> mFunctionalUnits = new HashMap<>();

        public Builder setNumberOfRegisters(int numberOfRegisters) {
            mNumberOfRegisters = numberOfRegisters;
            return this;
        }

        public Builder setFunctionalUnits(
                FunctionalUnit.Type type,
                int numberOfFunctionalUnits,
                int numberOfReserveStations) {
            mFunctionalUnits.put(type, new int[] {numberOfFunctionalUnits, numberOfReserveStations});
            return this;
        }

        public TomasuloCpu build() {
            return new TomasuloCpu(mNumberOfRegisters, mFunctionalUnits);
        }
    }
}
