package com.marula.cache.memory;

import com.marula.cache.cache.AssociativeSetCache;
import com.marula.cache.cache.Cache;
import com.marula.cache.cache.eviction.EvictionQueueFactory;
import com.marula.cache.cache.eviction.FifoLruEvictionQueue;
import com.marula.cache.utils.MemoryUtils;

import java.util.ArrayList;
import java.util.List;

public class TwoLevelCachedMemory implements Memory {

    private static final int L1_ACCESS_TIME = 2; // In time
    private static final int L2_ACCESS_TIME = 7; // In time
    private static final int MEMORY_ACCESS_TIME = 140; // In time
    //private static EvictionQueueFactory EVICTION_QUEUE_FACTORY = AgingLruEvictionQueue.FACTORY;
    private static EvictionQueueFactory EVICTION_QUEUE_FACTORY = FifoLruEvictionQueue.FACTORY;

    private final Cache mL1;
    private final Cache mL2;
    private final Memory mMemory = DynamicSizeMemory.createFullyAddressable();
    private final List<LogEntry> mHistory = new ArrayList<>();
    private boolean mAllowDumps = true;

    public TwoLevelCachedMemory(int l1Size, int l1BlocksPerSet, int l2Size, int l2BlocksPerSet) {
        mL1 = new AssociativeSetCache(l1Size, l1BlocksPerSet, EVICTION_QUEUE_FACTORY);
        mL2 = new AssociativeSetCache(l2Size, l2BlocksPerSet, EVICTION_QUEUE_FACTORY);
    }

    public TwoLevelCachedMemory setAllowDumps(boolean allowDumps) {
        mAllowDumps = allowDumps;
        return this;
    }

    @Override
    public int read(int address) {
        LogEntry log = new LogEntry(address);
        int blockAddress = MemoryUtils.blockAddress(address);
        int displacement = MemoryUtils.displacement(address);
        int[] block;
        if (mL1.contains(blockAddress)) {
            block = mL1.read(blockAddress);
            mL1.promote(blockAddress);
            log.set(CacheAction.READ_HIT_L1, L1_ACCESS_TIME);
        } else if (mL2.contains(blockAddress)) {
            block = mL2.read(blockAddress);
            mL2.evict(blockAddress);
            log.set(CacheAction.READ_HIT_L2, L2_ACCESS_TIME);
            cache(blockAddress, block, false, log);
        } else {
            block = MemoryUtils.readBlock(mMemory, blockAddress);
            log.set(CacheAction.READ_MISS, MEMORY_ACCESS_TIME);
            cache(blockAddress, block, false, log);
        }
        if (mAllowDumps) {
            log.l1Dump = mL1.toString();
            log.l2Dump = mL2.toString();
        }
        mHistory.add(log);
        return block[displacement];
    }

    @Override
    public void write(int address, int value) {
        LogEntry log = new LogEntry(address);
        int blockAddress = MemoryUtils.blockAddress(address);
        int displacement = MemoryUtils.displacement(address);
        if (mL1.contains(blockAddress)) {
            int[] block = mL1.read(blockAddress);
            block[displacement] = value;
            mL1.store(blockAddress, block); // Just to be explicit
            mL1.mark(blockAddress, true);
            mL1.promote(blockAddress);
            log.set(CacheAction.WRITE_HIT_L1, L1_ACCESS_TIME);
        } else if (mL2.contains(blockAddress)) {
            int[] block = mL2.read(blockAddress);
            block[displacement] = value;
            mL2.evict(blockAddress);
            log.set(CacheAction.WRITE_HIT_L2, L2_ACCESS_TIME);
            cache(blockAddress, block, true, log);
        } else {
            int[] block = MemoryUtils.readBlock(mMemory, blockAddress);
            block[displacement] = value;
            log.set(CacheAction.WRITE_MISS, MEMORY_ACCESS_TIME);
            cache(blockAddress, block, true, log);
        }
        if (mAllowDumps) {
            log.l1Dump = mL1.toString();
            log.l2Dump = mL2.toString();
        }
        mHistory.add(log);
    }

    private void cache(int blockAddress, int[] block, boolean dirty, LogEntry log) {
        Cache.Entry l1evicted = mL1.store(blockAddress, block);
        if (dirty) mL1.mark(blockAddress, true);
        if (l1evicted == null) return;
        log.actions.add(CacheAction.L1_EVICT_TO_L2);
        Cache.Entry l2evicted = mL2.store(l1evicted.blockAddress, l1evicted.block);
        if (l1evicted.dirty) mL2.mark(l1evicted.blockAddress, true);
        log.time += L2_ACCESS_TIME;
        if (l2evicted == null) return;
        if (l2evicted.dirty) {
            MemoryUtils.writeBlock(mMemory, l2evicted.blockAddress, l2evicted.block);
            log.time += MEMORY_ACCESS_TIME;
            log.actions.add(CacheAction.L2_EVICT_DIRTY);
        } else {
            log.actions.add(CacheAction.L2_EVICT_CLEAN);
        }
    }

    public void clearHistory() {
        mHistory.clear();
    }

    public List<LogEntry> getHistory() {
        return mHistory;
    }

    public static class LogEntry {

        public int address;
        public List<CacheAction> actions = new ArrayList<>();
        public int time;
        public String l1Dump;
        public String l2Dump;

        private LogEntry(int address) {
            this.address = address;
        }

        public void set(CacheAction action, int time) {
            actions.add(action);
            this.time = time;
        }
    }

    public static enum CacheAction {
        READ_HIT_L1,
        READ_HIT_L2,
        READ_MISS,
        WRITE_HIT_L1,
        WRITE_HIT_L2,
        WRITE_MISS,
        L1_EVICT_TO_L2,
        L2_EVICT_CLEAN,
        L2_EVICT_DIRTY
    }
}
