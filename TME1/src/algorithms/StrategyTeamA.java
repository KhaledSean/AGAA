package algorithms;

import robotsimulator.Brain;

public class StrategyTeamA extends Brain {
    private static final int TURNLEFTTASK = 1;
    private static final int MOVETASK = 2;
    private static final int TURNRIGHTTASK = 3;

    private int state;
    public void activate(){
        move();
    }
    public void step(){
        //ODOMETRY CODE

        move();
    }
}
