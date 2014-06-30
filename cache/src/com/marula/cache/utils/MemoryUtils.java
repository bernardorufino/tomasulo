package com.marula.cache.utils;

import com.marula.cache.Params;
import com.marula.cache.memory.Memory;

import static com.google.common.base.Preconditions.checkArgument;

public class MemoryUtils {

    public static int[] read(Memory memory, int address, int size) {
        int[] block = new int[size];
        for (int i = 0; i < size; i++) {
            block[i] = memory.read(address);
        }
        return block;
    }

    public static int[] readBlock(Memory memory, int blockAddress) {
        return read(memory, firstAddressOfBlock(blockAddress), Params.BLOCK_SIZE);
    }

    public static void writeBlock(Memory memory, int blockAddress, int[] block) {
        checkArgument(block.length == Params.BLOCK_SIZE, "block size must be Params.BLOCK_SIZE");

        int address = firstAddressOfBlock(blockAddress);
        for (int i = 0; i < Params.BLOCK_SIZE; i++) {
            memory.write(address, block[i]);
            address = address + 1;
        }
    }

    public static int blockAddress(int address) {
        return address >>> Params.BLOCK_BITS;
    }

    public static int displacement(int address) {
        return address & Params.DISPLACEMENT_MASK;
    }

    public static int firstAddressOfBlock(int blockAddress) {
        return blockAddress << Params.BLOCK_BITS;
    }

    // Prevents instantiation
    private MemoryUtils() {
        throw new AssertionError("Cannot instantiate object from " + this.getClass());
    }
}
