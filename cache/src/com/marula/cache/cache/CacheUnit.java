package com.marula.cache.cache;

import com.marula.cache.cache.eviction.EvictionQueue;

import java.util.*;

import static com.google.common.base.Preconditions.checkState;

public class CacheUnit implements Cache {

    private final int mMaxSize;
    private int mSize = 0;
    private final EvictionQueue mEvictionQueue;
    private final Map<Integer, int[]> mMemory = new HashMap<>();
    private final Set<Integer> mDirties = new HashSet<>();


    public CacheUnit(int maxSize, EvictionQueue evictionQueue) {
        mMaxSize = maxSize;
        mEvictionQueue = evictionQueue;
    }

    @Override
    public int[] read(int blockAddress) {
        return mMemory.get(blockAddress);
    }

    @Override
    public boolean contains(int blockAddress) {
        return mMemory.containsKey(blockAddress);
    }

    @Override
    public void promote(int blockAddress) {
        mEvictionQueue.promote(blockAddress);
    }

    @Override
    public Entry store(int blockAddress, int[] block) {
        Entry evicted = null;
        if (contains(blockAddress)) {
            mEvictionQueue.promote(blockAddress);
        } else {
            checkState(mSize <= mMaxSize);
            if (mSize == mMaxSize) {
                evicted = evict();
            }
            mSize = mSize + 1;
            mEvictionQueue.add(blockAddress);
        }
        mMemory.put(blockAddress, block);
        return evicted;
    }

    private Entry evict() {
        checkState(mSize > 0, "trying to evict block from empty cache");

        mSize = mSize - 1;
        int address = mEvictionQueue.evict();
        checkState(mMemory.containsKey(address));
        int[] entries = mMemory.remove(address);
        return new Entry(address, entries, mDirties.remove(address));
    }

    @Override
    public void evict(int blockAddress) {
        checkState(mSize > 0, "trying to evict block from empty cache");

        mSize = mSize - 1;
        mDirties.remove(blockAddress);
        mMemory.remove(blockAddress);
        mEvictionQueue.remove(blockAddress);
    }

    @Override
    public void mark(int blockAddress, boolean dirty) {
        if (dirty) mDirties.add(blockAddress);
        else mDirties.remove(blockAddress);
    }

    @Override
    public int size() {
        return mMemory.size();
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append(mMemory.size()).append(" -> [");
        Iterator<Integer> i = mEvictionQueue.iterator();
        while (i.hasNext()) {
            int address = i.next();
            string.append(address);
            if (mDirties.contains(address)) string.append("-D");
            if (i.hasNext()) {
                string.append(", ");
            }
        }
        string.append("]");
        return string.toString();
    }
}
