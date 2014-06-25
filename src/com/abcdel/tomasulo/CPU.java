package com.abcdel.tomasulo;

import java.util.List;

public class CPU {
    private final int RESERVE_STATION_LOAD_SIZE = 5;
    private final int RESERVE_STATION_ADD_SIZE = 3;
    private final int RESERVE_STATION_MULT_SIZE = 3;

    private Register[] registerBase = new Register[32];
    private List<Instruction> instructionList;
    private ReserveStation[] reserveStations = new ReserveStation[11];

    public CPU(List<Instruction> instructionList) {
        this.instructionList = instructionList;

        // For initial purposes only
        for(int i = 0 ; i < reserveStations.length ; i++){
            if(i < RESERVE_STATION_LOAD_SIZE)
                reserveStations[i] = new ReserveStation("ER" + i + 1, ReserveStation.Type.LOAD);
            else if(i<RESERVE_STATION_LOAD_SIZE + RESERVE_STATION_ADD_SIZE)
                reserveStations[i] = new ReserveStation("ER" + i + 1, ReserveStation.Type.ADD);
            else
                reserveStations[i] = new ReserveStation("ER" + i + 1, ReserveStation.Type.MULT);
        }
    }

    public void nextStep() {
        Instruction instruction = instructionList.get(0);

    }

    public ReserveStation[] getTable() {
        return reserveStations;
    }

}
