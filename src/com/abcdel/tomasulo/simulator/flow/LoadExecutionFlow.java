package com.abcdel.tomasulo.simulator.flow;

import com.abcdel.tomasulo.simulator.ReserveStation;
import com.abcdel.tomasulo.simulator.instruction.Instruction;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.base.Preconditions.checkState;

public class LoadExecutionFlow extends ExecutionFlow {

    private int mResult;

    @Override
    protected int issue(Instruction.Data i) {
        mCpu.loadStoreQueue.add(mRsIndex);
        if (mCpu.registerStatus[i.rs].Qi != null) {
            mReserveStation.Vj = 0;
            mReserveStation.Qj = mCpu.registerStatus[i.rs].Qi;
        } else {
            mReserveStation.Vj = mCpu.registers[i.rs];
            mReserveStation.Qj = null;
        }
        mReserveStation.A = i.imm;
        mReserveStation.busy = true;
        mCpu.registerStatus[i.rd].Qi = mReserveStation;
        return 0;
    }

    @Override
    protected boolean canExecute() {
        return mReserveStation.Qj == null && mCpu.loadStoreQueue.peek() == mRsIndex;
    }

    @Override
    protected int execute() {
        // Step 1
        mReserveStation.A = mReserveStation.Vj + mReserveStation.A;
        // Step 2
        int cycles = mMemory.cost(mReserveStation.A);
        mResult = mMemory.read(mReserveStation.A);
        // Pop queue
        postAtEndOfPhase(new Runnable() {
            @Override
            public void run() {
                int rs = mCpu.loadStoreQueue.remove();
                checkState(rs == mRsIndex);
            }
        });
        return cycles - 1;
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
        for (ReserveStation[] reserveStations : mCpu.reserveStations.values()) {
            for (ReserveStation rs : reserveStations) {
                if (rs.Qj == mReserveStation) {
                    rs.Vj = mResult;
                    rs.Qj = null;
                }
                if (rs.Qk == mReserveStation) {
                    rs.Vk = mResult;
                    rs.Qk = null;
                }
            }
        }
        mReserveStation.busy = false;
        return 0;
    }
}
