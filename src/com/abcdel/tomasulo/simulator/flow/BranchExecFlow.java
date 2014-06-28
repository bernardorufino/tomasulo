package com.abcdel.tomasulo.simulator.flow;

import com.abcdel.tomasulo.simulator.RegisterStat;
import com.abcdel.tomasulo.simulator.ReserveStation;
import com.abcdel.tomasulo.simulator.instruction.Branch;
import com.abcdel.tomasulo.simulator.instruction.Instruction;
import com.abcdel.tomasulo.simulator.memory.Memory;

import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkState;

public class BranchExecFlow extends FpExecFlow {

    public BranchExecFlow(Instruction instruction) {
        super(instruction);
    }

    @Override
    public int execute(ReserveStation[] RS, Memory mem, int r) {
        return 1;
    }

    @Override
    public int write(ReserveStation[] RS, RegisterStat[] registerStat, int[] Regs, Memory mem, int r, AtomicInteger pc) {
        Instruction instruction = getInstruction();
        checkState(instruction instanceof Branch, "instruction is not Branch");
        Branch branch = (Branch) instruction;
        branch.branch(RS[r].Vj, RS[r].Vk, pc);
        return 1;
    }
}
