package com.abcdel.tomasulo.simulator;

import com.abcdel.tomasulo.simulator.flow.ExecutionFlow;
import com.abcdel.tomasulo.simulator.instruction.Instruction;

public class ReserveStation {

    public boolean busy;
    public Instruction instruction;
    public int Vj;
    public int Vk;
    public ReserveStation Qj;
    public ReserveStation Qk;
    public int A;

    private int mIndex;
    private ExecutionFlow mExecutionFlow;
    private FunctionalUnit.Type mType;

    public ReserveStation(int index, FunctionalUnit.Type type) {
        mIndex = index;
        mType = type;
    }

    public void allocate(ExecutionFlow executionFlow) {
        deallocate();
        mExecutionFlow = executionFlow;
        busy = true;
        instruction = mExecutionFlow.getInstruction();
    }

    public void deallocate() {
        mExecutionFlow = null;
        busy = false;
        instruction = null;
        Vj = 0;
        Vk = 0;
        Qj = null;
        Qk = null;
        A = 0;
    }

    /* TODO: Needed by the UI, refactor to extract this info from here */

    public String getId() {
        return String.format("%s%d", mType, mIndex);
    }

    public FunctionalUnit.Type getType() {
        return mType;
    }

    public ExecutionFlow getExecutionFlow() {
        return mExecutionFlow;
    }
}
