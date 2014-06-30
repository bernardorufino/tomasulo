package com.abcdel.tomasulo.simulator.memory;

import java.util.HashMap;
import java.util.Map;

public class ConstantUniformAccessTimeMemory implements Memory {

    public static final int CYCLE_COST = 4;
    private static final int GARBAGE = 0x00000000;

    private Map<Integer, Integer> mMemory = new HashMap<>();

    @Override
    public int read(int address) {
        Integer value = mMemory.get(address);
        if (value == null) {
            value = address; /* TODO: Change address for GARBAGE  */
            mMemory.put(address, value);
        }
        return value;
    }

    @Override
    public void write(int address, int value) {
        mMemory.put(address, value);
    }

    @Override
    public int getLastAccessCost() {
        return CYCLE_COST;
    }
}
