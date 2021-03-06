package com.abcdel.tomasulo.ui.application.handlers.TableRowData;

import com.abcdel.tomasulo.simulator.RegisterStatus;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RegisterTableRow {

    private StringProperty mReg;
    private StringProperty mQ;
    private StringProperty mV;

    private RegisterTableRow(String reg, String q, String v) {
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

    public static class Builder {

        private String mReg = "";
        private String mQ = "";
        private String mV = "";

        public Builder setReg(int id) {
            mReg = String.valueOf(id);
            return this;
        }

        public Builder from(RegisterStatus registerStat) {
            mQ = (registerStat.Qi != null) ? registerStat.Qi.getId() : "-";
            mV = String.valueOf(registerStat.Vi);
            return this;
        }

        public RegisterTableRow build() {
            return new RegisterTableRow(mReg, mQ, mV);
        }
    }
}
