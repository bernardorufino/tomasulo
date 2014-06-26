package com.abcdel.tomasulo.ui;

public class ReserveStationTableRow {

  private String mID;
  private String mType;
  private String mBusy;
  private String mState;
  private String mVj;
  private String mVk;
  private String mQj;
  private String mQk;
  private String mA;

  public ReserveStationTableRow(String id, String type, String busy, String state, String vj, String vk, String qj, String qk, String a) {
    mA = a;
    mType = type;
    mBusy = busy;
    mID = id;
    mState = state;
    mVj = vj;
    mVk = vk;
    mQj = qj;
    mQk = qk;
  }
}
