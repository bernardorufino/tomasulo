package com.abcdel.tomasulo.simulator.flow;

import com.abcdel.tomasulo.simulator.Cpu;
import com.abcdel.tomasulo.simulator.memory.Memory;
import com.abcdel.tomasulo.simulator.RegisterStat;
import com.abcdel.tomasulo.simulator.ReserveStation;
import com.abcdel.tomasulo.simulator.instruction.IInstruction;
import com.abcdel.tomasulo.simulator.instruction.Instruction;

import static com.google.common.base.Preconditions.checkState;

public abstract class AbstractExecFlow implements ExecFlow {
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

    @Override
    public int run(Phase phase, ReserveStation[] RS, RegisterStat[] registerStat, int[] Regs, Memory mem, int r) {
        switch (phase) {
            case ISSUE: return issue(RS, registerStat, Regs, r);
            case EXECUTION: return execute(RS, mem, r);
            case WRITE: return write(RS, registerStat, Regs, mem, r);
        }
        throw new AssertionError();
    }

    protected Instruction getInstruction() {
        return instruction;
    }
}
