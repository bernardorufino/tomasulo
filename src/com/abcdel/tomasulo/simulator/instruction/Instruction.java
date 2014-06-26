package com.abcdel.tomasulo.simulator.instruction;

public interface Instruction {

    public int[] dependencies();

    public int assignee();

    /* TODO: Create execute */
}
