package com.abcdel.tomasulo.simulator.flow;

import com.abcdel.tomasulo.simulator.ReserveStation;
import com.abcdel.tomasulo.simulator.instruction.Instruction;
import com.abcdel.tomasulo.simulator.instruction.Instructions;
import com.abcdel.tomasulo.simulator.instruction.Lw;
import com.abcdel.tomasulo.simulator.instruction.Sw;
import com.abcdel.tomasulo.simulator.memory.Memory;
import com.abcdel.tomasulo.simulator.FunctionalUnit;
import com.abcdel.tomasulo.simulator.TomasuloCpu;
import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public abstract class ExecutionFlow implements Comparable<ExecutionFlow> {

    private static final List<Phase> PHASES_ORDER =
            ImmutableList.of(Phase.ISSUE, Phase.EXECUTION, Phase.WRITE);

    public static ExecutionFlow create(
            Instruction instruction,
            TomasuloCpu cpu,
            Memory memory,
            AtomicInteger branches) {
        FunctionalUnit.Type type = FunctionalUnit.Type.of(instruction);
        ExecutionFlow flow = null;
        switch (type) {
            case ADD:
            case MULT:
                flow = new FpExecutionFlow();
                break;
            case LOAD:
                if (instruction instanceof Lw) {
                    flow = new LoadExecutionFlow();
                } else {
                    checkState(instruction instanceof Sw, "instruction of type LOAD is not Lw nor Sw");
                    flow = new StoreExecutionFlow();
                }
                break;
        }
        checkState(flow != null, "Instruction does not yield valid flow"); assert flow != null;
        flow.onCreate(instruction, cpu, memory, branches);
        return flow;
    }

    protected Instruction mInstruction;
    protected TomasuloCpu mCpu;
    protected Memory mMemory;
    protected ReserveStation[] mReserveStations;
    protected FunctionalUnit[] mFunctionalUnits;
    protected int mRsIndex = TomasuloCpu.NO_RESERVE_STATION;
    protected ReserveStation mReserveStation = null;
    protected FunctionalUnit mFunctionalUnit = null;
    private AtomicInteger mBranches; /* TODO  */
    private Phase mNextPhase = Phase.ISSUE;
    private Phase mCurrentPhase = Phase.ISSUE;
    private FunctionalUnit.Type mType;
    private int mCountdown = 0;
    private Collection<Runnable> mEndOfCycleListeners = new HashSet<>();
    private Collection<Runnable> mEndOfPhaseListeners = new HashSet<>();

    protected ExecutionFlow() {
        /* Prevents outside instantiation, should use ExecutionFlow.create() instead */
    }

    private ExecutionFlow onCreate(
            Instruction instruction,
            TomasuloCpu cpu,
            Memory memory,
            AtomicInteger branches) {
        mInstruction = instruction;
        mType = FunctionalUnit.Type.of(mInstruction);
        mCpu = cpu;
        mReserveStations = mCpu.reserveStations.get(mType);
        checkNotNull(mReserveStations, "Reserve station for type " + mType + " not present");
        mFunctionalUnits = mCpu.functionalUnits.get(mType);
        checkNotNull(mReserveStations, "Functional units for type " + mType + " not present");
        mMemory = memory;
        mBranches = branches;
        onCreate();
        return this;
    }

    protected void onCreate() {
        /* Override */
    }

    public void clock(AtomicBoolean canWriteLock) {
        mEndOfCycleListeners.clear();
        if (mCountdown != 0) {
            mCountdown--;
            if (mCountdown == 0) {
                onLastCycleOfPhase();
            }
            return;
        }
        if (mNextPhase == null) return;
        int delay;
        mCurrentPhase = mNextPhase;
        switch (mNextPhase) {
            case ISSUE:
                boolean rsAllocated = false;
                for (int i = 0; i < mReserveStations.length; i++) {
                    if (!mReserveStations[i].busy) {
                        rsAllocated = true;
                        mReserveStation = mReserveStations[i];
                        mReserveStation.allocate(this);
                        mRsIndex = i;
                        break;
                    }
                }
                if (!rsAllocated) return;
                Instruction.Data data = Instructions.getAssociatedData(mInstruction);
                delay = issue(data);
                nextPhase(delay, Phase.EXECUTION);
                break;
            case EXECUTION:
                if (canExecute()) {
                    boolean fuAllocated = false;
                    for (FunctionalUnit fu : mFunctionalUnits) {
                        if (!fu.busy) {
                            fuAllocated = true;
                            mFunctionalUnit = fu;
                            mFunctionalUnit.busy = true;
                            break;
                        }
                    }
                    if (!fuAllocated) return;
                    delay = execute();
                    nextPhase(delay, Phase.WRITE);
                }
                break;
            case WRITE:
                mFunctionalUnit.busy = false;
                if (canWrite(canWriteLock)) {
                    delay = write();
                    nextPhase(delay, null);
                }
                break;
        }
    }

    private void onLastCycleOfPhase() {
        mEndOfCycleListeners.addAll(mEndOfPhaseListeners);
        mEndOfPhaseListeners.clear();
    }

    private void nextPhase(int delay, Phase nextPhase) {
        mCountdown = delay;
        mNextPhase = nextPhase;
        if (mCountdown == 0) {
            onLastCycleOfPhase();
        }
    }

    /* Override */
    protected boolean canWrite(AtomicBoolean canWriteLock) {
        return true;
    }

    /* Override */
    protected boolean canExecute() {
        return true;
    }

    /**
     * The return value of issue(), execute() and write() is the extra clock cycles until the instruction
     * goes to the next phase. For instance, if issue returns 1, then it will take the current clock cycle and
     * one more dumb clock cycle, and only on the next clock cycle it will run execute(). You can call it the delay
     * in cycles to change the current phase.
     */
    protected abstract int issue(Instruction.Data data);

    protected abstract int execute();

    protected abstract int write();

    protected void postAtEndOfCycle(Runnable runnable) {
        mEndOfCycleListeners.add(runnable);
    }

    protected void postAtEndOfPhase(Runnable runnable) {
        mEndOfPhaseListeners.add(runnable);
    }

    public Collection<Runnable> getEndOfCycleListeners() {
        return mEndOfCycleListeners;
    }

    public boolean hasBeenAllocated() {
        return mRsIndex != TomasuloCpu.NO_RESERVE_STATION;
    }

    public FunctionalUnit.Type getFuType() {
        return mType;
    }

    public boolean hasFinished() {
        return mNextPhase == null;
    }

    public Instruction getInstruction() {
        return mInstruction;
    }

    public Phase getPhase() {
        return mCurrentPhase;
    }

    public int compareTo(ExecutionFlow other) {
        int a = PHASES_ORDER.indexOf(mNextPhase);
        int b = PHASES_ORDER.indexOf(other.mNextPhase);
        return Integer.compare(a, b);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +  " for " + mInstruction.getClass().getSimpleName() + " at " + mNextPhase;
    }

    public static enum Phase {
        ISSUE, EXECUTION, WRITE
    }
}
