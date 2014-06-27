package com.abcdel.tomasulo.simulator.flow;

import com.abcdel.tomasulo.simulator.Memory;
import com.abcdel.tomasulo.simulator.RegisterStat;
import com.abcdel.tomasulo.simulator.ReserveStation;
import com.abcdel.tomasulo.simulator.instruction.Instruction;

public class LoadExecFlow extends AbstractExecFlow implements ExecFlow{
    public LoadExecFlow(Instruction instruction) {
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
        RS[r].A = imm;
        RS[r].busy = true;
        registerStat[rt].Qi = RS[r];
        return 1;
    }

    @Override
    public int execute(ReserveStation[] RS, RegisterStat[] registerStat, int[] Regs, Memory mem, int r) {
        // Step 1
        RS[r].A = RS[r].Vj + RS[r].A;
        // Step 2
        int execCycle = mem.checkAddress(RS[r].A);
        // In the beginning, this value must be 4,
        // and it will vary using the Project I
        return execCycle;
    }

    @Override
    public int write(ReserveStation[] RS, RegisterStat[] registerStat, int[] Regs, Memory mem, int r) {
        for(int i = 0 ; i < registerStat.length ; i++){
            if ( registerStat[i].Qi == RS[r] ) {
                // TODO implement the getResult method of Instruction
                //Regs[i] = String.valueOf(getInstruction().result);
                registerStat[i] = null;
            }
            if ( RS[i].Qj == RS[r] ) {
                //RS[i].Vj = String.valueOf(getInstruction().result);
                RS[i].Qj = null;
            }
            if ( RS[i].Qk == RS[r] ) {
                //RS[i].Vk = String.valueOf(getInstruction().result);
                RS[i].Qk = null;
            }
        }
        RS[r].busy = false;
        return 1;
    }
}
