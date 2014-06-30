package com.abcdel.tomasulo.simulator.instruction;

import com.abcdel.tomasulo.simulator.TomasuloCpu;

public interface Instruction {

    public int[] dependencies();

    public int assignee();

    public static class Data {

        // rd, rs, rt = 5 bits
        public int rd = TomasuloCpu.NO_REGISTER;
        public int rs = TomasuloCpu.NO_REGISTER;
        public int rt = TomasuloCpu.NO_REGISTER;
        // imm = 16 bits
        public int imm = 0;
    }

}
