package com.abcdel.tomasulo.ui;

import com.abcdel.tomasulo.simulator.ReserveStation;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ReserveStationTableRow {

    private StringProperty mID;
    private StringProperty mType;
    private StringProperty mBusy;
    private StringProperty mInstruction;
    private StringProperty mState;
    private StringProperty mVj;
    private StringProperty mVk;
    private StringProperty mQj;
    private StringProperty mQk;
    private StringProperty mA;

    private ReserveStationTableRow(String id, String type, String busy, String instruction, String state, String vj, String vk, String qj, String qk, String a) {
        mID = new SimpleStringProperty(id);
        mType = new SimpleStringProperty(type);
        mBusy = new SimpleStringProperty(busy);
        mInstruction = new SimpleStringProperty(instruction);
        mState = new SimpleStringProperty(state);
        mVj = new SimpleStringProperty(vj);
        mVk = new SimpleStringProperty(vk);
        mQj = new SimpleStringProperty(qj);
        mQk = new SimpleStringProperty(qk);
        mA = new SimpleStringProperty(a);
    }

    public StringProperty mIDProperty(){
        return mID;
    }

    public StringProperty mTypeProperty(){
        return mType;
    }

    public StringProperty mBusyProperty() {
        return mBusy;
    }

    public StringProperty mStateProperty() {
        return mState;
    }

    public StringProperty mVjProperty() {
        return mVj;
    }

    public StringProperty mVkProperty() {
        return mVk;
    }

    public StringProperty mQjProperty() {
        return mQj;
    }

    public StringProperty mQkProperty() {
        return mQk;
    }

    public StringProperty mAProperty() {
        return mA;
    }

    public static class Builder {
        private String mId;
        private String mType;
        private String mBusy;
        private String mInstruction;
        private String mState;
        private String mVj;
        private String mVk;
        private String mQj;
        private String mQk;
        private String mA;

        public Builder from(ReserveStation rs) {
            mId = rs.id;
            mType = rs.type;
            mBusy = String.valueOf(rs.busy);
            mInstruction = rs.instruction.getClass().getName();
            mState = rs.state.toSring();
            mVj = String.valueOf(rs.Vj);
            mVk = String.valueOf(rs.Vk);
            mQj = rs.Qj.id;
            mQk = rs.Qk.id;
            mA = String.valueOf(rs.A);
            return this;
        }

        public ReserveStationTableRow build() {
            return new ReserveStationTableRow(mId, mType, mBusy, mInstruction, mState, mVj, mVk, mQj, mQk, mA);
        }
    }
}
