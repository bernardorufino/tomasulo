package com.abcdel.tomasulo.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GeneralInformationTableRow {
    private StringProperty mField;
    private StringProperty mValue;

    public GeneralInformationTableRow(String field, String value) {
        mField = new SimpleStringProperty(field);
        mValue = new SimpleStringProperty(value);
    }

    public StringProperty mFieldProperty() {
        return mField;
    }

    public StringProperty mValueProperty() {
        return mValue;
    }
}
