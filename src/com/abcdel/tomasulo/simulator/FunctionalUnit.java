package com.abcdel.tomasulo.simulator;

import com.abcdel.tomasulo.simulator.instruction.*;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class FunctionalUnit {

    public static final Map<Class<? extends Instruction>, Type> INSTRUCTION_MAP =
            new ImmutableMap.Builder<Class<? extends Instruction>, Type>()
                    .put(Add.class, Type.ADD)
                    .put(Addi.class, Type.ADD)
                    .put(Beq.class, Type.BRANCH)
                    .put(Ble.class, Type.BRANCH)
                    .put(Bne.class, Type.BRANCH)
                    .put(Jmp.class, Type.BRANCH)
                    .put(Lw.class, Type.LOAD)
                    .put(Mul.class, Type.MULT)
                    .put(Nop.class, Type.UNDEFINED)
                    .put(Sub.class, Type.ADD)
                    .put(Sw.class, Type.LOAD)
                    .build();

    public boolean busy = false;

    public static enum Type {
        ADD, MULT, LOAD, BRANCH, UNDEFINED;

        public static Type of(Instruction instruction) {
            return INSTRUCTION_MAP.get(instruction.getClass());
        }
    }
}
