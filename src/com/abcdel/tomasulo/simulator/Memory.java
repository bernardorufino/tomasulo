package com.abcdel.tomasulo.simulator;

public interface Memory {

    public int read(int address);

    public void write(int address, int value);

    // return how many cycles it will cost to access this memory address
    public int checkAddress(int address);
}
