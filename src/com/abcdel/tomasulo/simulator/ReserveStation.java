package com.abcdel.tomasulo.simulator;

import com.abcdel.tomasulo.simulator.instruction.Instruction;

public class ReserveStation {

    public static int getTotalSize() {
        int size = 0;
        for (Type type : Type.values()) {
            size += type.size;
        }
        return size;
    }

    public String id;
    public String type;
    public boolean busy;
    public Instruction instruction;
    public int Vj;
    public int Vk;
    public ReserveStation Qj;
    public ReserveStation Qk;
    public int A;
    public State state;

    public static enum Type {
        LOAD(5),
        ADD(3),
        MULT(3);

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
