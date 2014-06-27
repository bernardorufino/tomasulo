package com.abcdel.tomasulo.simulator;

public interface Memory {

    public int read(int address);

    public int write(int address, int value);
}
