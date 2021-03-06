package com.abcdel.tomasulo.simulator.flow;

import com.abcdel.tomasulo.simulator.instruction.Instruction;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.base.Preconditions.checkState;

public class StoreExecutionFlow extends ExecutionFlow {

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
        if (mCpu.registerStatus[i.rt].Qi != null) {
            mReserveStation.Vk = 0;
            mReserveStation.Qk = mCpu.registerStatus[i.rt].Qi;
        } else {
            mReserveStation.Vk = mCpu.registers[i.rt];
            mReserveStation.Qk = null;
        }
        mReserveStation.A = i.imm;
        mReserveStation.busy = true;
        return 0;
    }

    @Override
    protected boolean canExecute() {
        return mReserveStation.Qj == null && mCpu.loadStoreQueue.peek() == mRsIndex;
    }

    @Override
    protected int execute() {
        mReserveStation.A = mReserveStation.Vj + mReserveStation.A;
        return 0;
    }

    @Override
    protected boolean canWrite(AtomicBoolean canWriteLock) {
        return mReserveStation.Qk == null;
    }

    @Override
    protected int write() {
        postAtEndOfPhase(new Runnable() {
            @Override
            public void run() {
                mReserveStation.busy = false;
                int rs = mCpu.loadStoreQueue.remove();
                checkState(rs == mRsIndex);
            }
        });
        // Writing should be done on write(), however we need the cost time here and there are no
        // side-effects since we have a queue for accessing memory and nobody can change Vk since Qk is null
        mMemory.write(mReserveStation.A, mReserveStation.Vk);
        return mMemory.getLastAccessCost() - 1;
    }
}
