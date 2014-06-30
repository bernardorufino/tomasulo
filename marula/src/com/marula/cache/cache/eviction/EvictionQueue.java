package com.marula.cache.cache.eviction;

public interface EvictionQueue extends Iterable<Integer> {

    public int evict();

    public void add(int address);

    public void promote(int address);

    public void remove(int address);
}
