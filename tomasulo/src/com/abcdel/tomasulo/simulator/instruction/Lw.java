package com.abcdel.tomasulo.simulator.instruction;

public class Lw extends IInstruction {

    public Lw(int rt, int rs, int imm) {
        super(rt, rs, imm);
    }

    @Override
    public int[] dependencies() {
        return new int[] {rs};
    }

    @Override
    public int assignee() {
        return rt;
    }
}
