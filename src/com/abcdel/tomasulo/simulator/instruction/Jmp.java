package com.abcdel.tomasulo.simulator.instruction;


import com.abcdel.tomasulo.simulator.TomasuloCpu;

import java.util.concurrent.atomic.AtomicInteger;

public class Jmp implements Instruction, Branch {

    public int imm;

    @Override
    public int[] dependencies() {
        return new int[0];
    }

    @Override
    public int assignee() {
        return TomasuloCpu.NO_REGISTER;
    }

    @Override
    public void branch(int r1, int r2, AtomicInteger pc) {
        pc.set(imm);
    }
}
