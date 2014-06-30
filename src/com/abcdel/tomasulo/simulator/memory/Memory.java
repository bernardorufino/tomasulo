package com.abcdel.tomasulo.simulator.memory;

public interface Memory {

    public int read(int address);

    public void write(int address, int value);

    // return how many cycles the last memory access has cost
    public int getLastAccessCost();
}
