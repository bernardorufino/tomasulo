package com.abcdel.tomasulo.simulator.instruction;

import com.abcdel.tomasulo.simulator.TomasuloCpu;

public class Nop extends RInstruction {

    public Nop() {
        super(TomasuloCpu.NO_REGISTER, TomasuloCpu.NO_REGISTER, TomasuloCpu.NO_REGISTER);
    }

    @Override
    public int[] dependencies() {
        return new int[0];
    }

    @Override
    public int assignee() {
        return TomasuloCpu.NO_REGISTER;
    }
}
