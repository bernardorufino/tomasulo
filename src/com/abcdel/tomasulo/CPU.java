package com.abcdel.tomasulo;

import java.util.List;

public class CPU {
  private Register[] registerBase = new Register[32];
  private List<Instruction> instructionList;
  private ReserveStation[] reserveStations = new ReserveStation[11];

  public CPU(List<Instruction> instructionList){
    this.instructionList = instructionList;

    // For initial purposes only
    for(int i = 0 ; i < 5 ; i++){
      reserveStations[i] = new ReserveStation("ER"+i+1,"Load/Store");
    }
    for(int i = 5 ; i < 8 ; i++){
      reserveStations[i] = new ReserveStation("ER"+i+1,"Load/Store");
    }
    for(int i = 8 ; i < 11 ; i++){
      reserveStations[i] = new ReserveStation("ER"+i+1,"Load/Store");
    }
  }

  public void nextStep() {
    Instruction instruction = instructionList.get(0);

  }

  public ReserveStation[] getTable(){
    return reserveStations;
  }

}
