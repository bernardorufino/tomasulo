package com.abcdel.tomasulo.simulator.instruction;

import com.abcdel.tomasulo.simulator.Cpu;

public class Ble extends IInstruction {

    public Ble(int rt, int rs, int imm) {
        super(rt, rs, imm);
    }

    @Override
    public int[] dependencies() {
        return new int[] {rs, rt};
    }

    @Override
    public int assignee() {
        return Cpu.NO_REGISTER;
    }
}
