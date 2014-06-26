package com.abcdel.tomasulo.simulator.flow;

import com.abcdel.tomasulo.simulator.Memory;
import com.abcdel.tomasulo.simulator.RegisterStat;
import com.abcdel.tomasulo.simulator.ReserveStation;

public interface ExecFlow {

    public int issue(ReserveStation[] RS, RegisterStat[] registerStat, int[] Regs, Memory mem, int r);

    public int execute(ReserveStation[] RS, RegisterStat[] registerStat, int[] Regs, Memory mem, int r);

    public int write(ReserveStation[] RS, RegisterStat[] registerStat, int[] Regs, Memory mem, int r);
}
