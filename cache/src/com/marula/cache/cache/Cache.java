package com.marula.cache.cache;

public interface Cache {

    public int[] read(int blockAddress);

    public boolean contains(int blockAddress);

    public void promote(int blockAddress);

    public Entry store(int blockAddress, int[] block);

    public void evict(int blockAddress);

    public void mark(int blockAddress, boolean dirty);

    public int size();

    public static class Entry {

        public int blockAddress;
        public int[] block;
        public boolean dirty;

        public Entry(int blockAddress, int[] block, boolean dirty) {
            this.blockAddress = blockAddress;
            this.block = block;
            this.dirty = dirty;
        }
    }
}
