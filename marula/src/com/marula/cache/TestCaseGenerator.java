package com.marula.cache;

import com.marula.cache.utils.MemoryUtils;

public class TestCaseGenerator {

    public static void main(String[] args) {
//        int x = MemoryUtils.firstAddressOfBlock(MemoryUtils.blockAddress(0x0041f7a0)) + Params.BLOCK_SIZE;
//        System.out.println(Integer.toHexString(x));
        System.out.println("block size = " + Params.BLOCK_SIZE);
//        int lastAddress = blocksInSet(1 << 8, 10, 18);
//        System.out.println(Integer.toHexString(lastAddress + 1) + " R");
        int setPrefix = (1 << 9) << (32 - 10);
        for (int i = 0; i < 19; i++) {
            int address = setPrefix | MemoryUtils.firstAddressOfBlock(i);
            System.out.println(Integer.toHexString(address) + " R");
        }
    }

    public static int blocksInSet(int setAddress, int setBits, int blocks) {
        // A set fits 2 blocks and has 9 bits for setAddress
        int setPrefix = setAddress << (32 - setBits);
        int address = 0;
        for (int i = 0; i < blocks; i++) {
            int blockPrefix = setPrefix | (i << 6);
            for (int j = 0; j < Params.BLOCK_SIZE; j++) {
                address = blockPrefix | j;
                System.out.println(Integer.toHexString(address) + " R");
            }
        }
        return address;
    }

}
