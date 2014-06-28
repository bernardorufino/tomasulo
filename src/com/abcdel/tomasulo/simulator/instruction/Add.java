package com.abcdel.tomasulo.simulator.instruction;

public class Add extends RInstruction implements Executable {

    public Add(int rd, int rs, int rt) {
        super(rd, rs, rt);
    }

    @Override
    public int execute(int a, int b) {
        return a + b;
    }
}
