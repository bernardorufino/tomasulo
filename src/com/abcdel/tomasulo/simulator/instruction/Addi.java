package com.abcdel.tomasulo.simulator.instruction;

public class Addi extends IInstruction implements Executable {

    public Addi(int rt, int rs, int imm) {
        super(rt, rs, imm);
    }

    @Override
    public int execute(int a, int b) {
        return a + b;
    }
}
