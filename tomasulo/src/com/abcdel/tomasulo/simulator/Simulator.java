package com.abcdel.tomasulo.simulator;

import com.abcdel.tomasulo.simulator.flow.ExecutionFlow;
import com.abcdel.tomasulo.simulator.instruction.Instruction;
import com.abcdel.tomasulo.simulator.memory.Memory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Simulator {

    private final TomasuloCpu mCpu;
    private final Memory mMemory;
    private final List<Instruction> mInstructions;
    private ExecutionFlow mPendingFlow;
    private PriorityQueue<ExecutionFlow> mFlows = new PriorityQueue<>();
    // When there is a branch, using integer in case we decide to allow more branch instructions in the pipeline
    private AtomicInteger mBranches = new AtomicInteger(0);
    private int mClock = 0;
    private int mInstructionsFinished = 0;

    public Simulator(TomasuloCpu cpu, Memory memory, List<Instruction> program) {
        mCpu = cpu;
        mMemory = memory;
        mInstructions = program;
        mPendingFlow = ExecutionFlow.create(nextInstruction(), mCpu, mMemory, mBranches);
        mFlows.add(mPendingFlow);
    }

    public boolean hasFinished() {
        return (mPendingFlow == null && mBranches.get() == 0) && mFlows.isEmpty();
    }

    public void clock() {
        mClock++;
        if (mBranches.get() == 0
                && (mPendingFlow == ExecutionFlow.NOP_FLOW
                || (mPendingFlow != null && mPendingFlow.hasBeenAllocated()))) {
            if (mPendingFlow.getFuType() == FunctionalUnit.Type.BRANCH) {
                mBranches.getAndIncrement();
            } else {
                mPendingFlow = getNextFlowAndAddTo(mFlows);
            }
        }
        // Emulates Common Data Bus
        AtomicBoolean canWriteLock = new AtomicBoolean(true);
        Collection<ExecutionFlow> toBeRemoved = new LinkedList<>();
        Collection<ExecutionFlow> toBeAdded = new LinkedList<>();
        Collection<Runnable> listeners = new LinkedList<>();
        for (ReserveStation rs : mCpu.allReserveStations()) {
            if (rs.busy) continue;
            rs.deallocate();
        }
        for (ExecutionFlow flow : mFlows) {
            flow.clock(canWriteLock);
            listeners.addAll(flow.getEndOfCycleListeners());
            if (flow.hasFinished()) {
                toBeRemoved.add(flow);
                if (flow.getFuType() == FunctionalUnit.Type.BRANCH) {
                    mBranches.getAndDecrement();
                    mPendingFlow = getNextFlowAndAddTo(toBeAdded);
                }
            }
        }
        for (Runnable listener : listeners) {
            listener.run();
        }
        mFlows.addAll(toBeAdded);
        mFlows.removeAll(toBeRemoved);
        mInstructionsFinished += toBeRemoved.size();
    }

    public int getClock() {
        return mClock;
    }

    private ExecutionFlow getNextFlowAndAddTo(Collection<? super ExecutionFlow> collection) {
        ExecutionFlow flow = nextFlow();
        if (flow != null && flow != ExecutionFlow.NOP_FLOW) {
            collection.add(flow);
        }
        return flow;
    }

    private ExecutionFlow nextFlow() {
        Instruction instruction = nextInstruction();
        if (instruction == null) return null;
        return ExecutionFlow.create(instruction, mCpu, mMemory, mBranches);
    }

    private Instruction nextInstruction() {
        int pc = mCpu.programCounter.get();
        if (pc / 4 >= mInstructions.size()) return null;
        Instruction instruction = mInstructions.get(pc / 4);
        mCpu.programCounter.set(pc + 4);
        return instruction;
    }

    public Instruction getCurrentInstruction() {
        int pc = mCpu.programCounter.get();
        if (pc / 4 >= mInstructions.size()) return null;
        return mInstructions.get(pc / 4);
    }

    public int getInstructionsFinished() {
        return mInstructionsFinished;
    }
}
