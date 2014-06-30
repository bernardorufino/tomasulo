package com.abcdel.tomasulo.simulator.memory;

import java.util.HashMap;
import java.util.Map;

public class ConstantUniformAccessTimeMemory implements Memory {

    public static final int CYCLE_COST = 4;
    private static final int GARBAGE = 0x00000000;

    private Map<Integer, Integer> mMemory = new HashMap<>();

    @Override
    public int read(int address) {
        Integer content = mMemory.get(address);
        if (content == null) {
            content = address; /* TODO: Change address for GARBAGE  */
            mMemory.put(address, content);
        }
        return content;
    }

    @Override
    public void write(int address, int value) {
        mMemory.put(address, value);
    }

    @Override
    public int cost(int address) {
        return CYCLE_COST;
    }
}
