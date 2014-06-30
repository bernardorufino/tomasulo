package com.marula.cache.utils;

import com.marula.cache.memory.TwoLevelCachedMemory;

import java.util.List;

public class HistoryUtils {

    public static String prettyHistory(List<TwoLevelCachedMemory.LogEntry> history, boolean withCacheDumps) {
        StringBuilder string = new StringBuilder();
        for (TwoLevelCachedMemory.LogEntry log : history) {
            string.append(Integer.toHexString(log.address)).append(": ").append(log.actions).append(" -> ").append(log.time).append(" clocks\n");
            if (withCacheDumps) {
                string.append("\nL1 dump: \n").append(log.l1Dump).append("\n");
                string.append("L2 dump: \n").append(log.l2Dump);
                string.append("\n\n\n");
            }
        }
        return string.toString();
    }

    public static String summary(List<TwoLevelCachedMemory.LogEntry> history) {
        Statistics stats = statistics(history);
        String string = "";
        string += String.format("L1 hits = %d, misses %d\n", stats.l1.hits, stats.l1.misses);
        string += String.format("L1 hit rate = %.2f\n", stats.l1.hitRate);
        string += String.format("L2 hits = %d, misses %d\n", stats.l2.hits, stats.l2.misses);
        string += String.format("L2 hit rate = %.2f\n", stats.l2.hitRate);
        string += String.format("Memory hits = %d\n", stats.memoryHits);
        string += String.format("total totalTime = %d clocks", stats.totalTime);
        return string;
    }

    public static Statistics statistics(List<TwoLevelCachedMemory.LogEntry> history) {
        CacheInfo l1 = new CacheInfo();
        CacheInfo l2 = new CacheInfo();
        int totalTime = 0;
        int memoryHits = 0;
        for (TwoLevelCachedMemory.LogEntry log : history) {
            totalTime += log.time;
            switch (log.actions.get(0)) {
                case READ_HIT_L1:
                case WRITE_HIT_L1:
                    l1.hits += 1;
                    break;
                case READ_HIT_L2:
                case WRITE_HIT_L2:
                    l1.misses += 1;
                    l2.hits += 1;
                    break;
                case READ_MISS:
                case WRITE_MISS:
                    memoryHits += 1;
                    l1.misses += 1;
                    l2.misses += 1;
                    break;
            }
        }
        l1.hitRate = (double) l1.hits / (l1.hits + l1.misses);
        l2.hitRate = (double) l2.hits / (l2.hits + l2.misses);
        return new Statistics(l1, l2, memoryHits, totalTime);
    }

    // Prevents instantiation
    private HistoryUtils() {
        throw new AssertionError("Cannot instantiate object from " + this.getClass());
    }

    private static class CacheInfo {

        public int misses;
        public int hits;
        public double hitRate;
    }

    public static class Statistics {

        public CacheInfo l1;
        public CacheInfo l2;
        public int memoryHits;
        public int totalTime;

        private Statistics(CacheInfo l1, CacheInfo l2, int memoryHits, int totalTime) {
            this.l1 = l1;
            this.l2 = l2;
            this.memoryHits = memoryHits;
            this.totalTime = totalTime;
        }
    }
}
