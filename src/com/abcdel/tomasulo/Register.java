package com.abcdel.tomasulo;

public class Register {
  private int value;
  private String dependence;

  public Register(){
    value = 0;
    dependence = "0";
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public String getDependence() {
    return dependence;
  }

  public void setDependence(String dependence) {
    this.dependence = dependence;
  }
}
