package com.abcdel.tomasulo.simulator.instruction;

import com.abcdel.tomasulo.simulator.Cpu;

public class Nop extends RInstruction {

    public Nop() {
        super(Cpu.NO_REGISTER, Cpu.NO_REGISTER, Cpu.NO_REGISTER);
    }

    @Override
    public int[] dependencies() {
        return new int[0];
    }

    @Override
    public int assignee() {
        return Cpu.NO_REGISTER;
    }
}
