package com.abcdel.tomasulo.simulator.flow;

import com.abcdel.tomasulo.simulator.Cpu;
import com.abcdel.tomasulo.simulator.instruction.Executable;
import com.abcdel.tomasulo.simulator.memory.Memory;
import com.abcdel.tomasulo.simulator.RegisterStat;
import com.abcdel.tomasulo.simulator.ReserveStation;
import com.abcdel.tomasulo.simulator.instruction.Instruction;
import com.abcdel.tomasulo.simulator.instruction.Mul;
import com.sun.org.apache.xpath.internal.operations.Div;

import static com.google.common.base.Preconditions.checkState;

public class FpExecFlow extends AbstractExecFlow implements ExecFlow {

    private int rd;
    private int mResult;

    public FpExecFlow(Instruction instruction) {
        super(instruction);
        rd = instruction.assignee();
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
        if(rt != Cpu.NO_REGISTER){
            if(registerStat[rt].Qi != null) {
                RS[r].Vk = 0;
                RS[r].Qk = registerStat[rt].Qi;
            } else {
                RS[r].Vk = Regs[rt];
                RS[r].Qk = null;
            }
        } else {
            RS[r].Vk = imm;
            RS[r].Qk = null;
        }
        RS[r].busy = true;
        registerStat[rd].Qi = RS[r];
        return 1;
    }

    @Override
    public int execute(ReserveStation[] RS, Memory mem, int r) {
        Instruction instruction = getInstruction();
        checkState(instruction instanceof Executable, "instruction is not executable");
        Executable executable = (Executable) instruction;
        mResult = executable.execute(RS[r].Vj, RS[r].Vk);
        if(instruction instanceof Div) return 5;
        if(instruction instanceof Mul) return 3;
        return 1;
    }

    @Override
    public int write(ReserveStation[] RS, RegisterStat[] registerStat, int[] Regs, Memory mem, int r) {
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
