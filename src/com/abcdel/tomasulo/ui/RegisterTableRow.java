package com.abcdel.tomasulo.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RegisterTableRow {
  private StringProperty mReg;
  private StringProperty mQ;
  private StringProperty mV;

  public RegisterTableRow(String reg, String q, String v) {
    mReg = new SimpleStringProperty(reg);
    mQ = new SimpleStringProperty(q);
    mV = new SimpleStringProperty(v);
  }

  public StringProperty mRegProperty() {
    return mReg;
  }

  public StringProperty mQProperty() {
    return mQ;
  }

  public StringProperty mVProperty() {
    return mV;
  }
}
