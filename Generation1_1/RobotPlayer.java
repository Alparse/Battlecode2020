package Generation1_1;

import battlecode.common.*;

import java.util.ArrayList;

public strictfp class RobotPlayer {
    static RobotController rc;

    static Direction[] directions = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTHEAST, Direction.NORTHWEST, Direction.SOUTHEAST, Direction.SOUTHWEST};
    static Direction[] leveeDirections = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTHEAST, Direction.NORTHWEST, Direction.SOUTHEAST, Direction.SOUTHWEST,Direction.CENTER};

    static int turnCount;
    static Team myTeam = null;
    static Team enemyTeam = null;
    static RobotType myType = null;
    static RobotInfo[] enemyRobots = null;
    static RobotInfo[] friendlyRobots = null;
    static MapLocation hqLoc = null;
    static MapLocation headQuarters = null;
    static MapLocation mother = null;
    static MapLocation myLoc;


    static Direction explore_Dir;
    static int myHeight = 0;
    static MapLocation lastLocation = null;
    static Direction obstacleDirection = null;

    static enum BugPathState {HUGRIGHT, HUGLEFT, NONE}

    static BugPathState bugPathState = null;
    static MapLocation bugEntry=null;
    static MapLocation lastBugEntry=null;
    static BugPathState lastBugState=null;
    static Direction lastBuggingDirection = null;
    static ArrayList<MapLocation> trail = null;
    static boolean message_submitted=false;
    static boolean message_accepted=false;
    static int message_bidAdder=0;

    static boolean netgunNear = false;
    static boolean fulfillmentCenterNear = false;
    static boolean designCenterNear = false;
    static boolean HQNear = false;
    static boolean refineryNear = false;
    static boolean enemyLandscaperNear = false;
    static boolean enemiesNear = false;
    static boolean vaporatorNear = false;
    static int design_centerBuilt=0;
    static int fulfillment_centerBuilt=0;
    static int vaporatorsBuilt=0;
    static int HQButtonedUp=0;
    static ArrayList<Message_Que> messageQue=new ArrayList<>();
    static ArrayList<MapLocation> largeWall=new ArrayList<>();




    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    static int width = 0;
    static int height = 0;
    static int[][] myMap = null;

    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        Generation1_1.RobotPlayer.rc = rc;
        turnCount = 0;
        myTeam = rc.getTeam();
        myType = rc.getType();
        if (myTeam == Team.A) {
            enemyTeam = Team.B;
        }
        if (myTeam == Team.B) {
            enemyTeam = Team.A;
        }

        height = rc.getMapHeight();
        width = rc.getMapWidth();

        myMap = new int[height][width];
        myLoc = rc.getLocation();
        myHeight = rc.senseElevation(myLoc);
        bugPathState = BugPathState.NONE;
        lastLocation = myLoc;
        trail = new ArrayList<MapLocation>();
        explore_Dir = Bug1.randomDirection();



        System.out.println("I'm a " + rc.getType() + " and I just got created!");
        sense_Mother_HQ();


        while (true) {
            turnCount += 1;
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                // Here, we've separated the controls into a different method for each RobotType.
                // You can add the missing ones or rewrite this into your own control structure.
                System.out.println("I'm a " + rc.getType() + "! Location " + rc.getLocation());
                switch (rc.getType()) {
                    case HQ:
                        HQ.runHQ();
                        break;
                    case MINER:
                        Miner3.runMiner();
                        break;
                    //case REFINERY:           runRefinery();          break;
                    //case VAPORATOR:          runVaporator();         break;
                    case DESIGN_SCHOOL:
                        DesignSchool.runDesignSchool();
                        break;
                    //case FULFILLMENT_CENTER: runFulfillmentCenter(); break;
                    case LANDSCAPER:         Landscaper2.runLandscaper();        break;
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

    static void sense_Mother_HQ() {
        RobotInfo[] nearby_Friendlies = rc.senseNearbyRobots(-1, myTeam);
        for (RobotInfo r : nearby_Friendlies) {
            if (r.type == RobotType.HQ) {
                headQuarters = r.location;
                hqLoc = r.location;
                mother = hqLoc;
                System.out.println("HQLOC " + hqLoc);
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
