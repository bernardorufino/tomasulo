package com.abcdel.tomasulo.simulator;

import com.abcdel.tomasulo.simulator.instruction.*;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class ReserveStation {

    public static final int NONE = -1;

    public static int getTotalSize() {
        int size = 0;
        for (Type type : Type.values()) {
            size += type.size;
        }
        return size;
    }

    public static final Map<Class<? extends Instruction>, Type> INSTRUCTION_MAP =
            new ImmutableMap.Builder<Class<? extends Instruction>, Type>()
                    .put(Add.class, Type.ADD)
                    .put(Addi.class, Type.ADD)
                    .put(Beq.class, Type.ADD)
                    .put(Ble.class, Type.ADD)
                    .put(Bne.class, Type.ADD)
                    .put(Jmp.class, Type.UNDEFINED)
                    .put(Lw.class, Type.LOAD)
                    .put(Mul.class, Type.MULT)
                    .put(Nop.class, Type.UNDEFINED)
                    .put(Sub.class, Type.ADD)
                    .put(Sw.class, Type.LOAD)
                    .build();

    private final int index;
    public boolean busy;
    public Instruction instruction;
    public int Vj;
    public int Vk;
    public ReserveStation Qj;
    public ReserveStation Qk;
    public int A;
    public State state;

    public ReserveStation(int index) {
        this.index = index;
    }

    public String getId() {
        Type type = Type.of(instruction);
        return String.format("%s%d", type, index);
    }

    public static enum Type {
        UNDEFINED(0),
        LOAD(5),
        ADD(3),
        MULT(3),
        BRANCH(3);

        public static Type of(Instruction instruction) {
            return INSTRUCTION_MAP.get(instruction.getClass());
        }

        public final int size;

        private Type(int size) {
            this.size = size;
        }
    }

    public static enum State {
        ISSUE("Issue"), EXECUTE("Execute"), WRITE("Write");

        private final String name;

        private State(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
