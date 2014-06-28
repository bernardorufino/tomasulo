package com.abcdel.tomasulo.simulator;

import com.abcdel.tomasulo.simulator.flow.ExecFlow;
import com.abcdel.tomasulo.simulator.flow.ExecFlowFactory;
import com.abcdel.tomasulo.simulator.instruction.Instruction;
import com.abcdel.tomasulo.simulator.memory.ConstantUniformAccessTimeMemory;
import com.abcdel.tomasulo.simulator.memory.Memory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.*;

/* TODO: Handle branches */

public class Cpu {

    public static final int NO_REGISTER = -1;
    private static final int REGISTERS = 32;

    private Memory mMemory = new ConstantUniformAccessTimeMemory();
    private final int[] mRegs = new int[REGISTERS];
    private final RegisterStat[] mRegisterStats = new RegisterStat[REGISTERS];
    private final Map<ReserveStation.Type, ReserveStation[]> mReserveStations = new HashMap<>();
    private final Map<Instruction, InstructionInfo> mInstructionState = new WeakHashMap<>();

    private final List<Instruction> mInstructions;
    private int mProgramCounter = 0;

    public Cpu(List<Instruction> instructions) {
        mInstructions = ImmutableList.copyOf(instructions);
        for (ReserveStation.Type type : ReserveStation.Type.values()) {
            mReserveStations.put(type, new ReserveStation[type.size]);
        }
    }

    public void nextStep() {
        Instruction nextInstruction = mInstructions.get(mProgramCounter / 4);
        ReserveStation[] reserveStations = mReserveStations.get(ReserveStation.Type.of(nextInstruction));
        int freeRs = getFreeReserveStation(reserveStations);
        if (freeRs != ReserveStation.NONE) {
            mProgramCounter += 4;
            ReserveStation rs = reserveStations[freeRs] = new ReserveStation(freeRs);
            rs.instruction = nextInstruction;
            ExecFlow execFlow = ExecFlowFactory.newInstance(nextInstruction);
            mInstructionState.put(nextInstruction, new InstructionInfo(
                    reserveStations, freeRs, execFlow, ExecFlow.Phase.ALLOCATED, 0));
        }
        Set<Instruction> toBeRemoved = new HashSet<>();
        for (Map.Entry<Instruction, InstructionInfo> entry : mInstructionState.entrySet()) {
            Instruction instruction = entry.getKey();
            InstructionInfo info = entry.getValue();
            if (info.counter != 0) {
                info.counter--;
            } else { // counter == 0
                if (info.phase == ExecFlow.Phase.WRITE) {
                    info.reserveStations[info.rsIndex] = null;
                    toBeRemoved.add(instruction);
                } else {
                    info.phase = info.phase.next;
                    info.counter = info.execFlow.run(info.phase,
                        info.reserveStations, mRegisterStats, mRegs, mMemory, info.rsIndex);
                }
            }
        }
        for (Instruction instruction : toBeRemoved) {
            mInstructionState.remove(instruction);
        }
    }

    private int getFreeReserveStation(ReserveStation[] reserveStations) {
        for (int i = 0; i < reserveStations.length; i++) {
            if (reserveStations[i] == null) return i;
        }
        return ReserveStation.NONE;
    }

    public Map<ReserveStation.Type, ReserveStation[]> getReserveStationsTable() {
        return ImmutableMap.copyOf(mReserveStations);
    }

    private class InstructionInfo {
        public ReserveStation[] reserveStations;
        public int rsIndex;
        public ExecFlow execFlow;
        public ExecFlow.Phase phase;
        public int counter;

        private InstructionInfo(
                ReserveStation[] reserveStations,
                int rsIndex,
                ExecFlow execFlow,
                ExecFlow.Phase phase,
                int counter) {
            this.reserveStations = reserveStations;
            this.rsIndex = rsIndex;
            this.execFlow = execFlow;
            this.phase = phase;
            this.counter = counter;
        }
    }
}
