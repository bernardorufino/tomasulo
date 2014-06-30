package com.abcdel.tomasulo.simulator.memory;

import com.marula.cache.memory.TwoLevelCachedMemory;

public class TwoLevelCachedMemoryDecorator implements Memory {

    private static final int L1_SIZE = 64 * 1024 / 4;
    private static final int L1_BLOCKS_PER_SET = 2;
    private static final int L2_SIZE = 1 * 1024 * 1024 / 4;
    private static final int L2_BLOCKS_PER_SET = 16;

    private TwoLevelCachedMemory mDecoratedMemory;
    private int mLastAccessCost;

    public TwoLevelCachedMemoryDecorator() {
        mDecoratedMemory = new TwoLevelCachedMemory(L1_SIZE, L1_BLOCKS_PER_SET, L2_SIZE, L2_BLOCKS_PER_SET);
    }

    @Override
    public int read(int address) {
        int value = mDecoratedMemory.read(address);
        cacheLastAccessCost();
        return value;
    }

    @Override
    public void write(int address, int value) {
        mDecoratedMemory.write(address, value);
        cacheLastAccessCost();
    }

    @Override
    public int getLastAccessCost() {
        return mLastAccessCost;
    }

    private void cacheLastAccessCost() {
        TwoLevelCachedMemory.LogEntry logEntry =
                mDecoratedMemory.getHistory().get(mDecoratedMemory.getHistory().size() - 1);
        mLastAccessCost = logEntry.time;
    }
}
