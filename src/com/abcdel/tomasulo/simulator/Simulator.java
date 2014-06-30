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
        if (mBranches.get() == 0 && mPendingFlow != null && mPendingFlow.hasBeenAllocated()) {
            if (mPendingFlow.getFuType() == FunctionalUnit.Type.BRANCH) {
                mBranches.getAndIncrement();
            } else {
                mPendingFlow = getNextFlowAndFeedQueue();
            }
        }
        // Emulates Common Data Bus
        AtomicBoolean canWriteLock = new AtomicBoolean(true);
        Collection<ExecutionFlow> toBeRemoved = new LinkedList<>();
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
                    mPendingFlow = getNextFlowAndFeedQueue();
                }
            }
        }
        for (Runnable listener : listeners) {
            listener.run();
        }
        for (ExecutionFlow flow : toBeRemoved) {
            mFlows.remove(flow);
        }
    }

    public int getClock() {
        return mClock;
    }

    private ExecutionFlow getNextFlowAndFeedQueue() {
        ExecutionFlow flow = nextFlow();
        if (flow != null) mFlows.add(flow);
        return flow;
    }

    private ExecutionFlow nextFlow() {
        Instruction instruction = nextInstruction();
        if (instruction == null) return null;
        return ExecutionFlow.create(instruction, mCpu, mMemory, mBranches);
    }

    private Instruction nextInstruction() {
        /* TODO: Die when instruction list is done */
        int pc = mCpu.programCounter.get();
        if (pc / 4 >= mInstructions.size()) return null;
        Instruction instruction = mInstructions.get(pc / 4);
        mCpu.programCounter.set(pc + 4);
        return instruction;
    }
}
