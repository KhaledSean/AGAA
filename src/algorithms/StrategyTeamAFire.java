package algorithms;

import characteristics.IRadarResult;
import robotsimulator.Brain;

public class StrategyTeamAFire extends Brain {
    private double fireSecondaryBot;
    public void activate(){
        //ODOMETRY
        for(IRadarResult o: detectRadar()){
           if (o.getObjectType()==IRadarResult.Types.TeamSecondaryBot)
               fire(o.getObjectDirection());
        }
        
    }
    public void step(){
        move();
    }
}
