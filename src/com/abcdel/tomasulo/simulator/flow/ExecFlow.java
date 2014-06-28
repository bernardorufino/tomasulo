package com.abcdel.tomasulo.simulator.flow;

import com.abcdel.tomasulo.simulator.memory.Memory;
import com.abcdel.tomasulo.simulator.RegisterStat;
import com.abcdel.tomasulo.simulator.ReserveStation;

import java.util.concurrent.atomic.AtomicInteger;

public interface ExecFlow {

    int run(Phase phase, ReserveStation[] RS, RegisterStat[] registerStat, int[] Regs, Memory mem, int r, AtomicInteger pc);

    public static enum Phase {
        ALLOCATED(Phase.ISSUE),
        ISSUE(Phase.EXECUTION),
        EXECUTION(Phase.WRITE),
        WRITE(null);

        public Phase next;

        private Phase(Phase next) {
            this.next = next;
        }
    }

    public int issue(ReserveStation[] RS, RegisterStat[] registerStat, int[] Regs, int r);

    public int execute(ReserveStation[] RS, Memory mem, int r);

    public int write(ReserveStation[] RS, RegisterStat[] registerStat, int[] Regs, Memory mem, int r, AtomicInteger pc);
}
