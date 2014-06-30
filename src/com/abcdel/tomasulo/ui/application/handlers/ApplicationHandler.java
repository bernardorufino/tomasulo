package com.abcdel.tomasulo.ui.application.handlers;

import com.abcdel.tomasulo.simulator.RegisterStat;
import com.abcdel.tomasulo.simulator.ReserveStation;
import com.abcdel.tomasulo.ui.application.MainApplication;
import javafx.scene.Node;

public interface ApplicationHandler {

    public void updateApplicationState(MainApplication.ApplicationState applicationState);
    public Node createPane();
    public void addListener(MainApplication.ApplicationListener listener);
    public void bind(ReserveStation[] reserveStations, RegisterStat[] registerStats, int clock);
}
