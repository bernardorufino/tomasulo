package com.abcdel.tomasulo.simulator.flow;

import com.abcdel.tomasulo.simulator.ReserveStation;
import com.abcdel.tomasulo.simulator.instruction.Executable;
import com.abcdel.tomasulo.simulator.instruction.Instruction;
import com.abcdel.tomasulo.simulator.instruction.Mul;
import com.abcdel.tomasulo.simulator.FunctionalUnit;
import com.abcdel.tomasulo.simulator.TomasuloCpu;
import com.sun.org.apache.xpath.internal.operations.Div;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.base.Preconditions.checkState;

public class FpExecutionFlow extends ExecutionFlow {

    private int mResult;

    @Override
    protected int issue(Instruction.Data i) {
        if (mCpu.registerStatus[i.rs].Qi != null) {
            mReserveStation.Vj = 0;
            mReserveStation.Qj = mCpu.registerStatus[i.rs].Qi;
        } else {
            mReserveStation.Vj = mCpu.registers[i.rs];
            mReserveStation.Qj = null;
        }
        if (i.rt == TomasuloCpu.NO_REGISTER) {
            mReserveStation.Vk = i.imm;
            mReserveStation.Qk = null;
        } else {
            if (mCpu.registerStatus[i.rt].Qi != null) {
                mReserveStation.Vk = 0;
                mReserveStation.Qk = mCpu.registerStatus[i.rt].Qi;
            } else {
                mReserveStation.Vk = mCpu.registers[i.rt];
                mReserveStation.Qk = null;
            }
        }
        mReserveStation.busy = true;
        mCpu.registerStatus[i.rd].Qi = mReserveStation;
        return 0;
    }

    @Override
    protected boolean canExecute() {
        return mReserveStation.Qj == null && mReserveStation.Qk == null;
    }

    @Override
    protected int execute() {
        checkState(mInstruction instanceof Executable, "Instruction is not executable");
        Executable executable = (Executable) mInstruction;
        mResult = executable.execute(mReserveStation.Vj, mReserveStation.Vk);
        // Remember I'm returning the delay, thus for the execution to take 3 cycles, I must return 2
        if (mInstruction instanceof Div) return 5 - 1;
        if (mInstruction instanceof Mul) return 3 - 1;
        if (getFuType() == FunctionalUnit.Type.ADD) return 1 - 1;
        return 1 - 1;
    }

    @Override
    protected boolean canWrite(AtomicBoolean canWriteLock) {
        return canWriteLock.getAndSet(false);
    }

    @Override
    protected int write() {
        for (int x = 0; x < mCpu.registerStatus.length; x++) {
            if (mCpu.registerStatus[x].Qi == mReserveStation) {
                mCpu.registers[x] = mResult;
                mCpu.registerStatus[x].Qi = null;
            }
        }
        for (ReserveStation rs : mCpu.allReserveStations()) {
            if (rs.Qj == mReserveStation) {
                rs.Vj = mResult;
                rs.Qj = null;
            }
            if (rs.Qk == mReserveStation) {
                rs.Vk = mResult;
                rs.Qk = null;
            }
        }
        mReserveStation.busy = false;
        return 0;
    }
}
