package Generation1_1;
import battlecode.common.*;

public strictfp class RobotPlayer {
    static RobotController rc;

    static Direction[] directions = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTHEAST,Direction.NORTHWEST, Direction.SOUTHEAST,Direction.SOUTHWEST};
    static RobotType[] spawnedByMiner = {RobotType.REFINERY, RobotType.VAPORATOR, RobotType.DESIGN_SCHOOL,
            RobotType.FULFILLMENT_CENTER, RobotType.NET_GUN};

    static int turnCount;
    static Team myTeam=null;
    static Team enemyTeam=null;
    static RobotType myType=null;
    static RobotInfo[] enemyRobots=null;
    static RobotInfo[] friendlyRobots=null;
    static MapLocation hqLoc=null;
    static MapLocation mother=null;
    static MapLocation myLoc;
    static MapLocation soupLoc=null;
    static boolean goingtoSoup=true;
    static boolean miningSoup=false;
    static boolean returningSoup=false;
    static boolean refiningSoup=false;
    static boolean exploring=false;
    static int miners_built = 0;
    static MapLocation enemy_hqLoc=null;
    static int xmin=99;
    static int ymin=99;
    static int xmax=99;
    static int ymax=99;
    static int explore_Steps=0;
    static Direction explore_Dir;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        Generation1_1.RobotPlayer.rc = rc;

        turnCount = 0;
        myTeam=rc.getTeam();
        myType=rc.getType();
        if (myTeam==Team.A) {
            enemyTeam = Team.B;}
        if (myTeam==Team.B)enemyTeam=Team.B;
        sense_Mother_HQ();


        System.out.println("I'm a " + rc.getType() + " and I just got created!");
        while (true) {
            turnCount += 1;
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                // Here, we've separated the controls into a different method for each RobotType.
                // You can add the missing ones or rewrite this into your own control structure.
                System.out.println("I'm a " + rc.getType() + "! Location " + rc.getLocation());
                switch (rc.getType()) {
                    case HQ:                 HQ.runHQ();             break;
                    case MINER:              Miner.runMiner();       break;
                    //case REFINERY:           runRefinery();          break;
                    //case VAPORATOR:          runVaporator();         break;
                    //case DESIGN_SCHOOL:      runDesignSchool();      break;
                    //case FULFILLMENT_CENTER: runFulfillmentCenter(); break;
                    //case LANDSCAPER:         runLandscaper();        break;
                    //case DELIVERY_DRONE:     runDeliveryDrone();     break;
                    //case NET_GUN:            runNetGun();            break;
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");

                e.printStackTrace();

            }
        }
    }

    static void sense_Mother_HQ(){
        RobotInfo[] nearby_Friendlies=rc.senseNearbyRobots(4,myTeam);
        for (RobotInfo r:nearby_Friendlies){
            if (myType==RobotType.MINER){
                if (r.type==RobotType.HQ){
                    hqLoc=r.location;
                    mother=hqLoc;
                }
            }
        }
    }



    static boolean tryRefine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        } else return false;
    }

    static void tryBlockchain() throws GameActionException {
        if (turnCount < 3) {
            int[] message = new int[10];
            for (int i = 0; i < 10; i++) {
                message[i] = 123;
            }
            if (rc.canSubmitTransaction(message, 10))
                rc.submitTransaction(message, 10);
        }
        // System.out.println(rc.getRoundMessages(turnCount-1));
    }


}
