package com.abcdel.tomasulo.simulator.instruction;

import com.abcdel.tomasulo.simulator.Cpu;

public class Bne extends IInstruction {

    public Bne(int rt, int rs, int imm) {
        super(rt, rs, imm);
    }

    @Override
    public int[] dependencies() {
        return new int[] {rs, rt};
    }

    @Override
    public int assignee() {
        return Cpu.NO_REGISTER;
    }
}