package com.abcdel.tomasulo.ents.instruction;

import com.abcdel.tomasulo.cpu.Cpu;

public class Jmp implements Instruction {

    public int imm;

    @Override
    public int[] dependencies() {
        return new int[0];
    }

    @Override
    public int assignee() {
        return Cpu.NO_REGISTER;
    }
}
