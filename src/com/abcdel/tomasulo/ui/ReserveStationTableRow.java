package com.abcdel.tomasulo.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ReserveStationTableRow {

    private StringProperty mID;
    private StringProperty mType;
    private StringProperty mBusy;
    private StringProperty mState;
    private StringProperty mVj;
    private StringProperty mVk;
    private StringProperty mQj;
    private StringProperty mQk;
    private StringProperty mA;

    public ReserveStationTableRow(String id, String type, String busy, String state, String vj, String vk, String qj, String qk, String a) {
        mID = new SimpleStringProperty(id);
        mType = new SimpleStringProperty(type);
        mBusy = new SimpleStringProperty(busy);
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
}
