package com.abcdel.tomasulo.simulator.instruction;

public class Sub extends RInstruction implements Executable {

    public Sub(int rd, int rs, int rt) {
        super(rd, rs, rt);
    }

    @Override
    public int execute(int a, int b) {
        return a - b;
    }
}
