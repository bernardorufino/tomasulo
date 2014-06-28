package com.abcdel.tomasulo.simulator.flow;

import com.abcdel.tomasulo.simulator.memory.Memory;
import com.abcdel.tomasulo.simulator.RegisterStat;
import com.abcdel.tomasulo.simulator.ReserveStation;

public interface ExecFlow {

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

    public int run(Phase phase, ReserveStation[] RS, RegisterStat[] registerStat, int[] Regs, Memory mem, int r);

    public int issue(ReserveStation[] RS, RegisterStat[] registerStat, int[] Regs, int r);

    public int execute(ReserveStation[] RS, Memory mem, int r);

    public int write(ReserveStation[] RS, RegisterStat[] registerStat, int[] Regs, Memory mem, int r);
}
