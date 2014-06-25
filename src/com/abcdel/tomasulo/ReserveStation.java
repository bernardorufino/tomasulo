package com.abcdel.tomasulo;

public class ReserveStation {
  public static enum TYPE {
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

  public ReserveStation(String ID, String type){
    this.ID = ID;
    this.type = type;
  }

  public boolean isBusy(){
    return busy != null;
  }

  public String[] getLine(){
    String[] line = {ID,type,busy,instruction,state,Vj,Vk,Qj,Qk,A};
    return line;
  }
}
