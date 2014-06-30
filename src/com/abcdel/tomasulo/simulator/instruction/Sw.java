package com.abcdel.tomasulo.simulator.instruction;

import com.abcdel.tomasulo.simulator.TomasuloCpu;

public class Sw extends IInstruction {

    public Sw(int rt, int rs, int imm) {
        super(rt, rs, imm);
    }

    @Override
    public int[] dependencies() {
        return new int[] {rs, rt};
    }

    @Override
    public int assignee() {
        return TomasuloCpu.NO_REGISTER;
    }
}
