package com.abcdel.tomasulo.simulator.instruction;

public class Instructions {

    public static Instruction.Data getAssociatedData(Instruction instruction) {
        Instruction.Data data = new Instruction.Data();
        int[] dependencies = instruction.dependencies();
        if (dependencies.length >= 1) {
            data.rs = dependencies[0];
            if (dependencies.length >= 2) {
                data.rt = dependencies[1];
            }
        }
        if (instruction instanceof IInstruction) {
            data.imm = ((IInstruction) instruction).imm;
        }
        data.rd = instruction.assignee();
        return data;
    }

    public static String toString(Instruction instruction) {
        /* TODO: Put attributes */
        return (instruction == null) ? "-" : instruction.getClass().getSimpleName();
    }

    // Prevents instantiation
    private Instructions() {
        throw new AssertionError("Cannot instantiate object from " + this.getClass());
    }
}
