package com.marula.cache.cache;

import com.marula.cache.Params;
import com.marula.cache.cache.eviction.EvictionQueueFactory;
import com.marula.cache.utils.MemoryUtils;

import static com.google.common.base.Preconditions.checkState;

public class AssociativeSetCache implements Cache {

    private static final boolean MOST_SIGNIFICANT_BITS_FOR_SET = false; // false = least significant bits

    private final Cache[] mCacheSets;

    public AssociativeSetCache(int size, int blocksPerSet, EvictionQueueFactory evictionQueueFactory) {
        int n = (int) (size / (blocksPerSet * Params.BLOCK_SIZE) + 0.5);
        mCacheSets = new Cache[n];
        for (int i = 0; i < n; i++) {
            mCacheSets[i] = new CacheUnit(blocksPerSet, evictionQueueFactory.create());
        }
    }

    @Override
    public int[] read(int blockAddress) {
        return getCacheSet(blockAddress).read(blockAddress);
    }

    @Override
    public boolean contains(int blockAddress) {
        return getCacheSet(blockAddress).contains(blockAddress);
    }

    @Override
    public void promote(int blockAddress) {
        getCacheSet(blockAddress).promote(blockAddress);
    }

    @Override
    public Entry store(int blockAddress, int[] block) {
        return getCacheSet(blockAddress).store(blockAddress, block);
    }

    @Override
    public void evict(int blockAddress) {
        getCacheSet(blockAddress).evict(blockAddress);
    }

    @Override
    public void mark(int blockAddress, boolean dirty) {
        getCacheSet(blockAddress).mark(blockAddress, dirty);
    }

    private Cache getCacheSet(int blockAddress) {
        // mCacheSets.length = 2^k such that k is integer
        int n = Integer.highestOneBit(mCacheSets.length);
        checkState(mCacheSets.length == n, "number of sets should be a power of 2");
        int k = Integer.numberOfTrailingZeros(n);
        int address = MemoryUtils.firstAddressOfBlock(blockAddress);
        address = address >>> (32 - (k - 1));
        checkState(address < n, "set address must be lower than n");
        return (MOST_SIGNIFICANT_BITS_FOR_SET) ? mCacheSets[address] : mCacheSets[blockAddress % mCacheSets.length];
    }

    public String toString() {
        StringBuilder string = new StringBuilder();
        int total = 0;
        for (int i = 0; i < mCacheSets.length; i++) {
            Cache set = mCacheSets[i];
            if (set.size() == 0) continue;
            total = total + 1;
            string.append(i).append(": ").append(set).append("\n");
        }
        string.append(total).append(" total sets\n");
        return string.toString();
    }

    @Override
    public int size() {
        int size = 0;
        for (Cache set : mCacheSets) {
            size += set.size();
        }
        return size;
    }
}
