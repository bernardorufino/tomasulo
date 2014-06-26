package com.abcdel.tomasulo.cpu;

import com.abcdel.tomasulo.ents.instruction.Instruction;
import com.abcdel.tomasulo.ents.RegisterStat;
import com.abcdel.tomasulo.ents.ReserveStation;

public class Cpu {

    public static final int NO_REGISTER = -1;
    private static final int REGISTERS = 32;

    private final int[] mRegs = new int[REGISTERS];
    private final RegisterStat[] mRegisterStats = new RegisterStat[REGISTERS];
    private final ReserveStation[] mReserveStations = new ReserveStation[ReserveStation.getTotalSize()];
    private final Iterable<Instruction> mInstructions;

    public Cpu(Iterable<Instruction> instructions) {
        mInstructions = instructions;
    }

    public void nextStep() {

    }

    public ReserveStation[] getReserveStationsTable() {
        return mReserveStations;
    }
}
