package com.abcdel.tomasulo.simulator;

import com.abcdel.tomasulo.simulator.instruction.*;
import com.abcdel.tomasulo.simulator.memory.ConstantUniformAccessTimeMemory;
import com.abcdel.tomasulo.simulator.memory.Memory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Main {

    public static void main(String[] args) {
        TomasuloCpu cpu = new TomasuloCpu(32, ImmutableMap.<FunctionalUnit.Type, int[]>builder()
                .put(FunctionalUnit.Type.ADD, new int[] {2, 2})
                .put(FunctionalUnit.Type.MULT, new int[] {1, 1})
                .put(FunctionalUnit.Type.LOAD, new int[] {2, 2})
                .build());
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
