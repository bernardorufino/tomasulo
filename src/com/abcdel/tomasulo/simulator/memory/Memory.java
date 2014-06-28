package com.abcdel.tomasulo.simulator.memory;

public interface Memory {

    public int read(int address);

    public void write(int address, int value);

    // return how many cycles it will cost to access this memory address
    public int cost(int address);
}
