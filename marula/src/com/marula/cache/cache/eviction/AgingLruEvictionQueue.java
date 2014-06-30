package com.marula.cache.cache.eviction;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

public class AgingLruEvictionQueue implements EvictionQueue {

  public static final EvictionQueueFactory FACTORY = new EvictionQueueFactory() {
    @Override
    public EvictionQueue create() {
      return new AgingLruEvictionQueue();
    }
  };

  private final Map<Integer, Node> mLookup = new HashMap<>();

  public AgingLruEvictionQueue() {
  }

  @Override
  public int evict() {
    checkState(mLookup.size() > 0,  "queue is empty");

    Node evicted = null;
    Iterator it = mLookup.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pairs = (Map.Entry)it.next();
      if(null == evicted || evicted.age > ((Node)pairs.getValue()).age){
        evicted = (Node)pairs.getValue();
      }
    }
    remove(evicted.address);
    return evicted.address;
  }

  @Override
  public void add(int address) {
    Node p = new Node(address);
    mLookup.put(address, p);
  }

  @Override
  public void promote(int address) {
    Node p = mLookup.get(address);

    Iterator it = mLookup.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pairs = (Map.Entry)it.next();
      ((Node)pairs.getValue()).age >>>=  1;
    }
    p.age |= (1 << 30);
  }

  @Override
  public void remove(int address) {
    mLookup.remove(address);
  }

  @Override
  public Iterator<Integer> iterator() {
    return (Iterator)mLookup.keySet().iterator();
  }

  private static class Node {

    public int address;
    public int age;

    private Node(int address) {
      this.address = address;
      this.age = 1 << 30;
    }
  }

}
