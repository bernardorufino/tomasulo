package com.abcdel.tomasulo.simulator.instruction;

import java.util.concurrent.atomic.AtomicInteger;

public interface Branch {

    public void branch(int r1, int r2, AtomicInteger pc);
}
