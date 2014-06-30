package com.abcdel.tomasulo.ui.application.handlers.TableRowData;

import com.abcdel.tomasulo.ui.application.MainApplication;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

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

    public static class Builder {

        private int mClock;
        private String mPC;
        private int mConcludedInstructionCount;
        private double mCPI;

        public List<GeneralInformationTableRow> buildAll() {
            List<GeneralInformationTableRow> genInfoTableRows = new ArrayList<>();
            genInfoTableRows.add(new GeneralInformationTableRow("Current Clock:", String.valueOf(mClock)));
            genInfoTableRows.add(new GeneralInformationTableRow("PC:", (mPC != null) ? mPC : "-"));
            genInfoTableRows.add(new GeneralInformationTableRow(
                    "Concluded Instruction Count:", String.valueOf(mConcludedInstructionCount)));
            genInfoTableRows.add(new GeneralInformationTableRow("CPI:", String.format("%.4f", mCPI)));
            return genInfoTableRows;
        }

        public Builder from(MainApplication.ApplicationData data) {
            mClock = data.clock;
            mPC = data.PC;
            mConcludedInstructionCount = data.concludedInstructionCount;
            mCPI = data.CPI;
            return this;
        }
    }
}
