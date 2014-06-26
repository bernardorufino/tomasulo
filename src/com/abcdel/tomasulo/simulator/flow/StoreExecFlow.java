package com.abcdel.tomasulo.simulator.flow;

import com.abcdel.tomasulo.simulator.Memory;
import com.abcdel.tomasulo.simulator.RegisterStat;
import com.abcdel.tomasulo.simulator.ReserveStation;
import com.abcdel.tomasulo.simulator.instruction.Instruction;

public class StoreExecFlow extends AbstractExecFlow implements ExecFlow{
    public StoreExecFlow(Instruction instruction) {
        super(instruction);
    }

    @Override
    public int issue(ReserveStation[] RS, RegisterStat[] registerStat, int[] Regs, Memory mem, int r) {
        return 0;
    }

    @Override
    public int execute(ReserveStation[] RS, RegisterStat[] registerStat, int[] Regs, Memory mem, int r) {
        return 0;
    }

    @Override
    public int write(ReserveStation[] RS, RegisterStat[] registerStat, int[] Regs, Memory mem, int r) {
        return 0;
    }
}
