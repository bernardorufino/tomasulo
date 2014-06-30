package com.abcdel.tomasulo.simulator.instruction;

import com.abcdel.tomasulo.simulator.TomasuloCpu;

import java.util.concurrent.atomic.AtomicInteger;

public class Ble extends IInstruction implements Branch {

    public Ble(int rt, int rs, int imm) {
        super(rt, rs, imm);
    }

    @Override
    public int[] dependencies() {
        return new int[] {rs, rt};
    }

    @Override
    public int assignee() {
        return TomasuloCpu.NO_REGISTER;
    }

    @Override
    public void branch(int r1, int r2, AtomicInteger pc) {
        if (r1 <= r2) {
            pc.set(imm);
        }
    }
}
