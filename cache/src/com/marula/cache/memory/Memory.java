package com.marula.cache.memory;

public interface Memory {

    public int read(int address);

    public void write(int address, int value);
}
