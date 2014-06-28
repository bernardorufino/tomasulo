package com.abcdel.tomasulo.simulator.flow;

import com.abcdel.tomasulo.simulator.memory.Memory;
import com.abcdel.tomasulo.simulator.RegisterStat;
import com.abcdel.tomasulo.simulator.ReserveStation;
import com.abcdel.tomasulo.simulator.instruction.Instruction;

import java.util.concurrent.atomic.AtomicInteger;

public class LoadExecFlow extends AbstractExecFlow implements ExecFlow{

    private int mResult;

    public LoadExecFlow(Instruction instruction) {
        super(instruction);
    }

    @Override
    public int issue(ReserveStation[] RS, RegisterStat[] registerStat, int[] Regs, int r) {
        if(registerStat[rs].Qi != null) {
            RS[r].Vj = 0;
            RS[r].Qj = registerStat[rs].Qi;
        } else {
            RS[r].Vj = Regs[rs];
            RS[r].Qj = null;
        }
        RS[r].A = imm;
        RS[r].busy = true;
        registerStat[rt].Qi = RS[r];
        return 1;
    }

    @Override
    public int execute(ReserveStation[] RS, Memory mem, int r) {
        // Step 1
        RS[r].A = RS[r].Vj + RS[r].A;
        // Step 2
        int execCycle = mem.cost(RS[r].A);
        mResult = mem.read(RS[r].A);
        // In the beginning, this value must be 4,
        // and it will vary using the Project I
        return execCycle;
    }

    @Override
    public int write(ReserveStation[] RS, RegisterStat[] registerStat, int[] Regs, Memory mem, int r, AtomicInteger pc) {
        for(int i = 0 ; i < registerStat.length ; i++){
            if ( registerStat[i].Qi == RS[r] ) {
                // TODO implement the getResult method of Instruction
                Regs[i] = mResult;
                registerStat[i] = null;
            }
            // TODO: Doesn't accessing RS[] require another loop?
            if ( RS[i].Qj == RS[r] ) {
                RS[i].Vj = mResult;
                RS[i].Qj = null;
            }
            if ( RS[i].Qk == RS[r] ) {
                RS[i].Vk = mResult;
                RS[i].Qk = null;
            }
        }
        RS[r].busy = false;
        return 1;
    }
}
