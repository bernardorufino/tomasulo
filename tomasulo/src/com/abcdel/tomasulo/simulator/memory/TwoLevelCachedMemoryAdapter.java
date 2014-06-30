package com.abcdel.tomasulo.simulator.memory;

import com.marula.cache.memory.TwoLevelCachedMemory;

public class TwoLevelCachedMemoryAdapter implements Memory {

    private TwoLevelCachedMemory mBackingMemory;
    private int mLastAccessCost;

    public TwoLevelCachedMemoryAdapter(TwoLevelCachedMemory memory) {
        mBackingMemory = memory;
    }

    @Override
    public int read(int address) {
        int value = mBackingMemory.read(address);
        cacheLastAccessCost();
        return value;
    }

    @Override
    public void write(int address, int value) {
        mBackingMemory.write(address, value);
        cacheLastAccessCost();
    }

    private void cacheLastAccessCost() {
        TwoLevelCachedMemory.LogEntry logEntry =
                mBackingMemory.getHistory().get(mBackingMemory.getHistory().size() - 1);
        mLastAccessCost = logEntry.time;
    }

    @Override
    public int getLastAccessCost() {
        return mLastAccessCost;
    }
}
