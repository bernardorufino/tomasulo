package com.abcdel.tomasulo.simulator.flow;

import com.abcdel.tomasulo.simulator.Cpu;
import com.abcdel.tomasulo.simulator.Memory;
import com.abcdel.tomasulo.simulator.RegisterStat;
import com.abcdel.tomasulo.simulator.ReserveStation;
import com.abcdel.tomasulo.simulator.instruction.Instruction;
import com.abcdel.tomasulo.simulator.instruction.Mul;
import com.sun.org.apache.xpath.internal.operations.Div;

public class FpExecFlow extends AbstractExecFlow implements ExecFlow {
    private int rd;

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
        if(instruction instanceof Div) return 5;
        if(instruction instanceof Mul) return 3;
        return 1;
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
