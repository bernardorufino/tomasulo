package com.abcdel.tomasulo.simulator.memory;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class RecentsTrackerMemoryDecorator implements Memory {

    private final Memory mBackingMemory;
    private final int mRecentsSize;
    /* TODO: 3 maps, really? */
    private Map<Integer, Entry> mCacheMap = new HashMap<>();
    private TreeMap<Integer, Entry> mRecentMemory = new TreeMap<>(new Comparator<Integer>() {
        @Override
        public int compare(Integer a, Integer b) {
            return mCacheMap.get(a).compareTo(mCacheMap.get(b));
        }
    });
    private Map<Integer, Integer> mTransformedMap;

    public RecentsTrackerMemoryDecorator(Memory memory, int recentsToTrack) {
        mBackingMemory = memory;
        mTransformedMap = Maps.transformValues(mRecentMemory.descendingMap(), new Function<Entry, Integer>() {
            @Override
            public Integer apply(Entry entry) {
                return entry.value;
            }
        });
        mRecentsSize = recentsToTrack;
    }

    /* Ugly as hell =( */

    private void put(int address, Entry entry) {
        mCacheMap.put(address, entry);
        mRecentMemory.put(address, entry);
    }

    private void removeFirstEntry() {
        int address = mRecentMemory.firstEntry().getKey();
        mRecentMemory.remove(address);
        mCacheMap.remove(address);
    }

    private Entry remove(int address) {
        if (!mCacheMap.containsKey(address)) return null;
        mRecentMemory.remove(address);
        return mCacheMap.remove(address);
    }

    private Entry update(int address, int value) {
        Entry entry = remove(address);
        if (entry == null) {
            entry = new Entry();
        }
        entry.value = value;
        entry.accessedAt = System.currentTimeMillis();
        put(address, entry);
        while (mRecentMemory.size() > mRecentsSize) {
            removeFirstEntry();
        }
        return entry;
    }

    @Override
    public int read(int address) {
        int value = mBackingMemory.read(address);
        update(address, value);
        return value;
    }

    @Override
    public void write(int address, int value) {
        mBackingMemory.write(address, value);
        update(address, value);
    }

    @Override
    public int getLastAccessCost() {
        return mBackingMemory.getLastAccessCost();
    }

    public Map<Integer, Integer> getRecentMemory() {
        return ImmutableMap.copyOf(mTransformedMap);
    }

    private class Entry implements Comparable<Entry> {

        public int value;
        public long accessedAt;

        @Override
        public int compareTo(Entry other) {
            return Long.compare(accessedAt, other.accessedAt);
        }
    }
}
