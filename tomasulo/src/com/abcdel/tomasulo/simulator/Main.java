package com.abcdel.tomasulo.simulator;

import com.abcdel.tomasulo.simulator.instruction.*;
import com.abcdel.tomasulo.simulator.memory.ConstantUniformAccessTimeMemory;
import com.abcdel.tomasulo.simulator.memory.Memory;
import com.google.common.collect.ImmutableList;

public class Main {

    public static void main(String[] args) {
        TomasuloCpu cpu = new TomasuloCpu.Builder()
                .setNumberOfRegisters(32)
                .setFunctionalUnits(FunctionalUnit.Type.ADD, 3, 3)
                .setFunctionalUnits(FunctionalUnit.Type.MULT, 2, 2)
                .setFunctionalUnits(FunctionalUnit.Type.LOAD, 2, 2)
                .build();
        Memory memory = new ConstantUniformAccessTimeMemory();
        Simulator simulator = new Simulator(cpu, memory, ImmutableList.<Instruction>of(
                new Lw(6, 2, 34),
                new Lw(2, 3, 45),
                new Mul(0, 2, 4),
                new Sub(8, 6, 2),
                new Mul(10, 0, 6),
                new Add(6, 8, 2)));
        for (int i = 0; i < 10; i++) {
            simulator.clock();
            System.out.println("clock " + i);
        }
    }

}
