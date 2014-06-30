package com.abcdel.tomasulo.simulator.flow;

import com.abcdel.tomasulo.simulator.TomasuloCpu;
import com.abcdel.tomasulo.simulator.instruction.Branch;
import com.abcdel.tomasulo.simulator.instruction.Instruction;

import static com.google.common.base.Preconditions.checkState;

public class BranchExecutionFlow extends ExecutionFlow {

    @Override
    protected int issue(Instruction.Data i) {
        if (i.rs == TomasuloCpu.NO_REGISTER) { // Jmp
            mReserveStation.Vj = i.imm;
            mReserveStation.Qj = null;
        } else { // Beq, Ble, Bne
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
        }
        mReserveStation.busy = true;
        return 0;
    }

    @Override
    protected boolean canExecute() {
        return mReserveStation.Qj == null && mReserveStation.Qk == null;
    }

    @Override
    protected int execute() {
        return 1 - 1;
    }

    @Override
    protected int write() {
        checkState(mInstruction instanceof Branch, "Instruction does not implement Branch");
        Branch branch = (Branch) mInstruction;
        branch.branch(mReserveStation.Vj, mReserveStation.Vk, mCpu.programCounter);
        mReserveStation.busy = false;
        return 0;
    }
}
