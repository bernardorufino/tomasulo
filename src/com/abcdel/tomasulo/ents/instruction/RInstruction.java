package com.abcdel.tomasulo.ents.instruction;

public class RInstruction implements Instruction {

    public int rd;
    public int rs;
    public int rt;

    public RInstruction(int rd, int rs, int rt) {
        this.rd = rd;
        this.rs = rs;
        this.rt = rt;
    }

    @Override
    public int[] dependencies() {
        return new int[] {rs, rt};
    }

    @Override
    public int assignee() {
        return rd;
    }
}
