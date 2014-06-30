package com.marula.cache.cache.eviction;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

public class FifoLruEvictionQueue implements EvictionQueue {

    public static final boolean EXACT_LRU = true;

    public static final EvictionQueueFactory FACTORY = new EvictionQueueFactory() {
        @Override
        public EvictionQueue create() {
            return new FifoLruEvictionQueue();
        }
    };

    private final Map<Integer, Node> mLookup = new HashMap<>();
    private Node mLeft = new Node(null, 0, null);
    private Node mRight = new Node(null, 0, null);

    public FifoLruEvictionQueue() {
        mLeft.next = mRight;
        mRight.previous = mLeft;
    }

    @Override
    public int evict() {
        checkState(mLookup.size() > 0,  "queue is empty");

        return remove(mLeft.next).address;
    }

    @Override
    public void add(int address) {
        Node p = new Node(mRight.previous, address, mRight);
        mRight.previous.next = p;
        mRight.previous = p;
        mLookup.put(address, p);
    }

    @Override
    public void promote(int address) {
        if (!EXACT_LRU) return;

        Node p = mLookup.get(address);

        // Remove from current position
        remove(p);

        // Insert into right side
        mRight.previous.next = p;
        p.next = mRight;
        p.previous = mRight.previous;
        mRight.previous = p;
    }

    private Node remove(Node p) {
        p.previous.next = p.next;
        p.next.previous = p.previous;
        return p;
    }

    public void remove(int address) {
        remove(mLookup.get(address));
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("[");
        for (Node p = mLeft.next; p != mRight; p = p.next) {
            string.append(Integer.toHexString(p.address));
            if (p.next != mRight) {
                string.append(", ");
            }
        }
        string.append("]");
        return string.toString();
    }

    @Override
    public Iterator<Integer> iterator() {
        return new InternalIterator();
    }

    private static class Node {

        public int address;
        public Node previous;
        public Node next;

        private Node(Node previous, int address, Node next) {
            this.previous = previous;
            this.address = address;
            this.next = next;
        }
    }

    private class InternalIterator implements Iterator<Integer> {

        private Node mCurrent = mLeft.next;

        @Override
        public boolean hasNext() {
            return mCurrent != mRight;
        }

        @Override
        public Integer next() {
            int ans = mCurrent.address;
            mCurrent = mCurrent.next;
            return ans;
        }

        @Override
        public void remove() {
            FifoLruEvictionQueue.this.remove(mCurrent);
        }
    }
}
