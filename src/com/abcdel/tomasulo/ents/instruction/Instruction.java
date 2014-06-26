package com.abcdel.tomasulo.ents.instruction;

public interface Instruction {

    public int[] dependencies();

    public int assignee();

    /* TODO: Create execute */
}
