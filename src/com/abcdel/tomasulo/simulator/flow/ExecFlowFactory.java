package com.abcdel.tomasulo.simulator.flow;

import com.abcdel.tomasulo.simulator.ReserveStation;
import com.abcdel.tomasulo.simulator.instruction.Instruction;
import com.abcdel.tomasulo.simulator.instruction.Lw;
import com.abcdel.tomasulo.simulator.instruction.Sw;

import static com.google.common.base.Preconditions.checkState;

public class ExecFlowFactory {

    public static ExecFlow newInstance(Instruction instruction) {
        ReserveStation.Type type = ReserveStation.Type.of(instruction);
        switch (type) {
            case ADD:
            case MULT:
                return new FpExecFlow(instruction);
            case LOAD:
                if (instruction instanceof Lw) {
                    return new LoadExecFlow(instruction);
                } else {
                    checkState(instruction instanceof Sw, "instruction of type LOAD is not Lw nor Sw");
                    return new StoreExecFlow(instruction);
                }
        }
        throw new AssertionError("instruction does not yield valid ExecFlow");
    }

    // Prevents instantiation
    private ExecFlowFactory() {
        throw new AssertionError("Cannot instantiate object from " + this.getClass());
    }
}
