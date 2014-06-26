package com.abcdel.tomasulo.ents.instruction;

public class IInstruction implements Instruction {

    public int rt;
    public int rs;
    public int imm;

    public IInstruction(int rt, int rs, int imm) {
        this.rt = rt;
        this.rs = rs;
        this.imm = imm;
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
