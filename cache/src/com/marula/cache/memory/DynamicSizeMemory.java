package com.marula.cache.memory;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public class DynamicSizeMemory implements Memory {

    private static final long MASK = 0x00000000FFFFFFFFL;
    private static final int DEFAULT_VALUE = 0;

    public static DynamicSizeMemory createFullyAddressable() {
        return new DynamicSizeMemory(1L << 32);
    }

    private final Map<Integer, Integer> mMemory = new HashMap<>();
    private final long mSize;

    public DynamicSizeMemory(long size) {
        mSize = size;
    }

    @Override
    public int read(int address) {
        checkAddress(address);
        return mMemory.containsKey(address) ? mMemory.get(address) : DEFAULT_VALUE;
    }

    @Override
    public void write(int address, int value) {
        checkAddress(address);
        mMemory.put(address, value);
    }

    private void checkAddress(int address) {
        long longAddress = ((long) address) & MASK;
        checkArgument(longAddress < mSize, "address out of bounds");
    }
}
