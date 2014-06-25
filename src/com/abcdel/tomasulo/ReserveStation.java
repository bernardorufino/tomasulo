package com.abcdel.tomasulo;

public class ReserveStation {
    public static enum Type {
        LOAD("Load/Store"),
        ADD("Add"),
        MULT("Mult");

        public final String type;

        Type(String type){
            this.type = type;
        }
    };

    private String ID;
    private String type;
    private String busy;
    private String instruction;
    private String state;
    private String Vj;
    private String Vk;
    private String Qj;
    private String Qk;
    private String A;

    public ReserveStation(String ID, Type type) {
        this.ID = ID;
        this.type = type.toString();
    }

    public boolean isBusy() {
        return busy != null;
    }

    public String[] getLine() {
        String[] line = {ID, type, busy, instruction, state, Vj, Vk, Qj, Qk, A};
        return line;
    }
}
