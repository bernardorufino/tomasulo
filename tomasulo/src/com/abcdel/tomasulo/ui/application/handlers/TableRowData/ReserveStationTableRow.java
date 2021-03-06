package com.abcdel.tomasulo.ui.application.handlers.TableRowData;

import com.abcdel.tomasulo.simulator.ReserveStation;
import com.abcdel.tomasulo.simulator.flow.ExecutionFlow;
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
    private StringProperty mExecTime;

    private ReserveStationTableRow(String id, String type, String busy, String instruction, String state, String vj, String vk, String qj, String qk, String a, String executionTime) {
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
        mExecTime = new SimpleStringProperty(executionTime);
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

    public StringProperty mInstructionProperty() {
        return mInstruction;
    }

    public StringProperty mExecTimeProperty() {
        return mExecTime;
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
        private String mExecutionTime;

        public Builder from(ReserveStation rs) {
            ExecutionFlow flow = rs.getExecutionFlow();
            mId = rs.getId();
            mType = (rs.getType() != null) ? rs.getType().toString() : "-";
            mBusy = String.valueOf(rs.busy);
            mInstruction = (rs.instruction != null) ? rs.instruction.getClass().getSimpleName() : "-";
            mState = "-";
            if (flow != null) {
                if (flow.isWaiting()) {
                    mState = "WAITING";
                } else if (flow.getPhase() != null) {
                    mState = flow.getPhase().toString();
                }
            }
            mVj = String.valueOf(rs.Vj);
            mVk = String.valueOf(rs.Vk);
            mQj = (rs.Qj != null) ? rs.Qj.getId() : "-";
            mQk = (rs.Qk != null) ? rs.Qk.getId() : "-";
            mA = String.valueOf(rs.A);
            mExecutionTime = "-";
            if (flow != null && !flow.isWaiting() && flow.getExecutionTime() != ExecutionFlow.NOT_TIMED) {
                mExecutionTime = Integer.toString(flow.getExecutionTime());
            }
            return this;
        }

        public ReserveStationTableRow build() {
            return new ReserveStationTableRow(mId, mType, mBusy, mInstruction, mState, mVj, mVk, mQj, mQk, mA, mExecutionTime);
        }
    }
}
