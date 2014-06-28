package com.abcdel.tomasulo.ui.application.handlers.TableRowData;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RecentlyUsedMemoryTableRow {

  private StringProperty mAddress;
  private StringProperty mValue;

  public RecentlyUsedMemoryTableRow(String address, String value) {
    mAddress = new SimpleStringProperty(address);
    mValue = new SimpleStringProperty(value);
  }

  public StringProperty mAddressProperty() {
    return mAddress;
  }

  public StringProperty mValue() {
    return mValue;
  }
}
