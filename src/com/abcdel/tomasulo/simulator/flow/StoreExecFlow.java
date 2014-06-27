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
        if(registerStat[rs].Qi != null) {
            RS[r].Vj = 0;
            RS[r].Qj = registerStat[rs].Qi;
        } else {
            RS[r].Vj = Regs[rs];
            RS[r].Qj = null;
        }
        if(registerStat[rt].Qi != null) {
            RS[r].Vk = 0;
            RS[r].Qk = registerStat[rt].Qi;
        } else {
            RS[r].Vk = Regs[rt];
            RS[r].Qk = null;
        }
        RS[r].A = imm;
        RS[r].busy = true;

        return 1;
    }

    @Override
    public int execute(ReserveStation[] RS, RegisterStat[] registerStat, int[] Regs, Memory mem, int r) {
        RS[r].A = RS[r].Vj + RS[r].A;
        // In the beginning, this value must be 4,
        // and it will vary using the Project I
        int execCycle = mem.checkAddress(RS[r].A);
        return execCycle;
    }

    @Override
    public int write(ReserveStation[] RS, RegisterStat[] registerStat, int[] Regs, Memory mem, int r) {
        mem.write(RS[r].A,RS[r].Vk);
        RS[r].busy = false;
        return 1;
    }
}
