package com.abcdel.tomasulo.simulator.instruction;

public class Mul extends RInstruction implements Executable {

    public Mul(int rd, int rs, int rt) {
        super(rd, rs, rt);
    }

    @Override
    public int execute(int a, int b) {
        return a * b;
    }
}
