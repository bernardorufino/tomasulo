package com.abcdel.tomasulo.simulator.flow;

import com.abcdel.tomasulo.simulator.Cpu;
import com.abcdel.tomasulo.simulator.instruction.IInstruction;
import com.abcdel.tomasulo.simulator.instruction.Instruction;

import static com.google.common.base.Preconditions.checkState;

public class AbstractExecFlow{
    private Instruction instruction;
    protected int rs;
    protected int rt;
    protected int imm;

    public AbstractExecFlow(Instruction instruction){
        this.instruction = instruction;

        int[] dependencies = instruction.dependencies();
        rs = dependencies[0];
        if (dependencies.length == 2) {
            imm = 0;
            rt = dependencies[1];
        } else {
            checkState(instruction instanceof IInstruction);
            imm = ((IInstruction) instruction).imm;
            rt = Cpu.NO_REGISTER;
        }
    }

    protected Instruction getInstruction() {
        return instruction;
    }
}
