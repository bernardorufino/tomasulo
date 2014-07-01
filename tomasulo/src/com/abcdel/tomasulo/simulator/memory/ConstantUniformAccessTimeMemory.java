package com.abcdel.tomasulo.simulator.memory;

import java.util.HashMap;
import java.util.Map;

public class ConstantUniformAccessTimeMemory implements Memory {

    private static final int GARBAGE = 0x00000000;

    private final int mCycleCost;

    private Map<Integer, Integer> mMemory = new HashMap<>();

    public ConstantUniformAccessTimeMemory(int cycleCost) {
        mCycleCost = cycleCost;
    }

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
        return mCycleCost;
    }
}
