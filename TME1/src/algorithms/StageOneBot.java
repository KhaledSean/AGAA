package algorithms;

import characteristics.IFrontSensorResult;
import characteristics.IRadarResult;
import characteristics.Parameters;
import robotsimulator.Brain;

public class StageOneBot extends Brain {
    //---PARAMETERS---//
    private static final double HEADINGPRECISION = 0.001;
    private static final double ANGLEPRECISION = 0.1;

    private static final int ROCKY = 0x1EADDA;
    private static final int MARIO = 0x5EC0;
    private static final int UNDEFINED = 0xBADC0DE0;

    private static final int TURNLEFTTASK = 1;
    private static final int MOVETASK = 2;
    private static final int TURNRIGHTTASK = 3;
    private static final int SINK = 0xBADC0DE1;

    //---VARIABLES---//
    private int state;
    private double oldAngle;
    private double myX,myY;
    private boolean isMoving;
    private int whoAmI;

    //---CONSTRUCTORS---//
    public StageOneBot() { super(); }

    //---ABSTRACT-METHODS-IMPLEMENTATION---//
    public void activate() {
        //ODOMETRY CODE
        whoAmI = ROCKY;
        for (IRadarResult o: detectRadar())
            if (isSameDirection(o.getObjectDirection(),Parameters.NORTH)) whoAmI=MARIO;
        if (whoAmI == ROCKY){
            myX=Parameters.teamASecondaryBot1InitX;
            myY=Parameters.teamASecondaryBot1InitY;
        } else {
            myX=0;
            myY=0;
        }

        //INIT
        state=(whoAmI==ROCKY)?TURNLEFTTASK:SINK;
        isMoving=false;
        oldAngle=getHeading();
    }
    public void step() {
        //ODOMETRY CODE
        if (isMoving && whoAmI == ROCKY){
            myX+=Parameters.teamASecondaryBotSpeed*Math.cos(getHeading());
            myY+=Parameters.teamASecondaryBotSpeed*Math.sin(getHeading());
            isMoving=false;
        }
        //DEBUG MESSAGE
        if (whoAmI == ROCKY) {
            sendLogMessage("#ROCKY *thinks* he is rolling at position ("+(int)myX+", "+(int)myY+").");
        }

        //AUTOMATON
        if (state==TURNLEFTTASK && !(isSameDirection(getHeading(),Parameters.NORTH))) {
            stepTurn(Parameters.Direction.LEFT);
            //sendLogMessage("Initial TeamA Secondary Bot1 position. Heading North!");
            return;
        }
        if (state==TURNLEFTTASK && isSameDirection(getHeading(),Parameters.NORTH)) {
            state=MOVETASK;
            myMove();
            //sendLogMessage("Moving a head. Waza!");
            return;
        }
        if (state==MOVETASK && detectFront().getObjectType()!=IFrontSensorResult.Types.WALL) {
            myMove(); //And what to do when blind blocked?
            //sendLogMessage("Moving a head. Waza!");
            return;
        }
        if (state==MOVETASK && detectFront().getObjectType()== IFrontSensorResult.Types.WALL) {
            state=TURNRIGHTTASK;
            oldAngle=getHeading();
            stepTurn(Parameters.Direction.RIGHT);
            //sendLogMessage("Iceberg at 12 o'clock. Heading 3!");
            return;
        }
        if (state==TURNRIGHTTASK && !(isSameDirection(getHeading(),oldAngle+Parameters.RIGHTTURNFULLANGLE))) {
            stepTurn(Parameters.Direction.RIGHT);
            //sendLogMessage("Iceberg at 12 o'clock. Heading 3!");
            return;
        }
        if (state==TURNRIGHTTASK && isSameDirection(getHeading(),oldAngle+Parameters.RIGHTTURNFULLANGLE)) {
            state=MOVETASK;
            myMove();
            //sendLogMessage("Moving a head. Waza!");
            return;
        }

        if (state==SINK) {
            myMove();
            return;
        }
        if (true) {
            return;
        }
    }
    private void myMove(){
        isMoving=true;
        move();
    }
    private boolean isSameDirection(double dir1, double dir2){
        return Math.abs(normalize(dir1)-normalize(dir2))<ANGLEPRECISION;
    }
    private double normalize(double dir){
        double res=dir;
        while (res<0) res+=2*Math.PI;
        while (res>=2*Math.PI) res-=2*Math.PI;
        return res;
    }
}
