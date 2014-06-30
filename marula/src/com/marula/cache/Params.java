package com.marula.cache;

public class Params {

    public static final int BLOCK_BITS = 6;
    public static final int BLOCK_SIZE = 1 << BLOCK_BITS;
    public static final int DISPLACEMENT_MASK = -1 >>> (32 - Params.BLOCK_BITS);
    public static final int BLOCK_MASK = ~DISPLACEMENT_MASK;

    // Prevents instantiation
    private Params() {
        throw new AssertionError("Cannot instantiate object from " + this.getClass());
    }
}
