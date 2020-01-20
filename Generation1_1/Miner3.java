package Generation1_1;

import battlecode.common.*;

import java.util.Arrays;
import java.util.stream.StreamSupport;


public class Miner3 extends RobotPlayer {
    static RobotController rc = RobotPlayer.rc;
    static MapLocation soupLoc = null;

    static boolean construction_worker = false;
    static boolean foundSoup = false;
    static boolean goingtoSoup = false;
    static boolean miningSoup = false;
    static boolean returningSoup = false;
    static boolean refiningSoup = false;
    static boolean designCenterBuilt = false;
    static MapLocation refineryLock = null;
    static int minerJob = 0;
    static MapLocation lastSoupLoc = null;

    enum minerState {SCANNINGSOUP, SEARCHINGSOUP, GOINGSOUP, MININGSOUP, RETURNINGSOUP, DEPOSITINGSOUP, BUILDINGREFINERY}

    static minerState myState = minerState.SCANNINGSOUP;

    static void runMiner() throws GameActionException {
        friendlyRobots = rc.senseNearbyRobots(-1, myTeam);
        mother_Nearby();

        if (hqLoc == null) {
            hqLoc=Communications.getHqLocFromBlockchain();
            refineryLock = hqLoc;
        }
        if (rc.getRoundNum() > 0) {
            minerJob = Communications.getMinerJobFromBlockchain();
        }
        System.out.println("MY JOB IS " + minerJob);

        while (true) {
            try {
                System.out.println("BYTECODE START " + Clock.getBytecodeNum());
                myLoc = rc.getLocation();
                myHeight = rc.senseElevation(myLoc);
                friendlyRobots = rc.senseNearbyRobots(-1, myTeam);
                enemyRobots = rc.senseNearbyRobots(-1, enemyTeam);
                Utility.friendlyRobotScan();
                Utility.enemyRobotScan();


                if (minerJob == 1) {
                    System.out.println("I AM A CONSTRUCTOR "+(myLoc.distanceSquaredTo(hqLoc)));
                    if (!designCenterNear && !designCenterBuilt && myLoc.distanceSquaredTo(hqLoc) > 8&&myLoc.distanceSquaredTo(hqLoc)<=10) {
                        System.out.println("TESTER TRUE");
                        for (Direction dir : directions)
                            if (rc.isReady() && Utility.tryBuild(RobotType.DESIGN_SCHOOL, dir)) {
                                hqLoc = myLoc.add(dir);
                                designCenterNear = true;
                                designCenterBuilt = true;
                                minerJob = 0;
                                break;
                            }
                    }

                    if (myLoc.distanceSquaredTo(hqLoc) > 13) {
                        explore_Dir=myLoc.directionTo(hqLoc);
                    }
                    if (myLoc.distanceSquaredTo(hqLoc)<=13&&myLoc.distanceSquaredTo(hqLoc)>8){
                        explore_Dir=myLoc.directionTo(hqLoc).rotateRight().rotateRight();
                    }
                    if (myLoc.distanceSquaredTo(hqLoc)<=8){
                        System.out.println("<8");
                        explore_Dir=myLoc.directionTo(hqLoc).opposite();
                    }
                    if(rc.isReady()&&rc.canMove(explore_Dir)){
                        makeMove(explore_Dir);
                    }
                    if(rc.isReady()&&!rc.canMove(explore_Dir)){
                        while(!rc.canMove(explore_Dir)) {
                            explore_Dir = explore_Dir.rotateRight();
                        }
                        makeMove(explore_Dir);
                    }
                }

                if (minerJob == 0) {
                    System.out.println("I AM A MINER");

                    switch (myState) {
                        case SCANNINGSOUP:
                            System.out.println(minerState.SCANNINGSOUP);
                            if (soupLoc != null) {
                                myState = minerState.GOINGSOUP;
                                break;
                            }
                            MapLocation[] nearbySoup = rc.senseNearbySoup();
                            if (nearbySoup.length == 0) {
                                myState = minerState.SEARCHINGSOUP;
                            }
                            if (nearbySoup.length > 0) {
                                MapLocation nearestSoup = null;
                                int range_Soup = 999;
                                for (MapLocation l : nearbySoup) {
                                    int new_range_Soup = l.distanceSquaredTo(myLoc);
                                    if (new_range_Soup < range_Soup) {
                                        nearestSoup = l;
                                        range_Soup = new_range_Soup;
                                        soupLoc = l;
                                        myState = minerState.GOINGSOUP;
                                    }
                                }
                            }
                            break;
                        case SEARCHINGSOUP:
                            System.out.println(minerState.SEARCHINGSOUP);
                            while (!rc.canMove(explore_Dir)) {
                                explore_Dir = randomDirection();
                            }
                            makeMove(explore_Dir);
                            myState = minerState.SCANNINGSOUP;
                            break;

                        case GOINGSOUP:
                            myLoc=rc.getLocation();
                            myHeight=rc.senseElevation(myLoc);
                            //scanForRefinery();
                            System.out.println(minerState.GOINGSOUP);
                            if (myLoc.isAdjacentTo(soupLoc) || myLoc == soupLoc) {
                                myState = minerState.MININGSOUP;
                            } else {
                                Direction move_dir = Bug1.BugGetNext(soupLoc);
                                makeMove(move_dir);
                            }

                            break;

                        case MININGSOUP:
                            System.out.println(minerState.MININGSOUP);
                            if (rc.canMineSoup(myLoc.directionTo(soupLoc))) {
                                if (rc.isReady()) {
                                    rc.mineSoup(myLoc.directionTo(soupLoc));
                                }
                            }
                            if (rc.getSoupCarrying() == RobotType.MINER.soupLimit) {
                                myState = minerState.RETURNINGSOUP;
                            }
                            if (rc.senseSoup(soupLoc) == 0) {
                                soupLoc = null;
                                findSoup();
                                myState = minerState.SCANNINGSOUP;
                            }
                            break;

                        case RETURNINGSOUP:
                            myLoc=rc.getLocation();
                            myHeight=rc.senseElevation(myLoc);
                            System.out.println(minerState.RETURNINGSOUP);
                            if (myLoc.distanceSquaredTo(refineryLock) < 9) {
                                if (Utility.isWalledOff(refineryLock)) {
                                    System.out.println("DESTINATION WALLED OFF");
                                    myState = minerState.BUILDINGREFINERY;
                                    RobotInfo currentRefinery = rc.senseRobotAtLocation(refineryLock);
                                    for (RobotInfo r : friendlyRobots) {
                                        if (r.ID != currentRefinery.ID && (r.type == RobotType.REFINERY || r.type == RobotType.HQ)) {
                                            refineryLock = r.location;
                                            System.out.println("FOUND ANOTHER REFINERY " + refineryLock);
                                            myState = minerState.RETURNINGSOUP;
                                        }
                                    }

                                }
                            }
                            //if(myLoc.distanceSquaredTo(refineryLock)>140&&!refineryNear){
                           // myState=minerState.BUILDINGREFINERY;
                            //}
                            Direction move_dir = Bug1.BugGetNext(refineryLock);
                            makeMove(move_dir);
                            if (myLoc.isAdjacentTo(refineryLock)) {
                                myState = minerState.DEPOSITINGSOUP;
                            }
                            break;

                        case DEPOSITINGSOUP:
                            System.out.println(minerState.DEPOSITINGSOUP + " at " + refineryLock);
                            if (rc.isReady() && rc.canDepositSoup(myLoc.directionTo(refineryLock))) {
                                rc.depositSoup(myLoc.directionTo(refineryLock), RobotType.MINER.soupLimit);
                            }
                            if (rc.getSoupCarrying() == 0) {
                                myState = minerState.SCANNINGSOUP;
                            }
                            break;

                        case BUILDINGREFINERY:
                            System.out.println(minerState.BUILDINGREFINERY);
                            if(refineryNear){
                                for (RobotInfo r:friendlyRobots){
                                    if(r.type==RobotType.REFINERY){
                                        refineryLock=r.location;
                                        refineryNear=true;
                                    }
                                }
                                if (rc.getSoupCarrying() > 0) {
                                    myState = minerState.RETURNINGSOUP;
                                }
                                if (rc.getSoupCarrying() == 0) {
                                    myState = minerState.SCANNINGSOUP;
                                }
                                break;
                            }
                            for (Direction dir : directions) {
                                if (rc.isReady() && Utility.tryBuild(RobotType.REFINERY, dir)) {
                                    refineryLock = myLoc.add(dir);
                                    refineryNear = true;
                                    if (rc.getSoupCarrying() > 0) {
                                        myState = minerState.DEPOSITINGSOUP;
                                    }
                                    break;
                                }
                            }
                            if (rc.getSoupCarrying() == 0) {
                                myState = minerState.SCANNINGSOUP;
                            }
                            break;

                    }

                }

                System.out.println("BYTECODE END " + Clock.getBytecodeNum());
            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");

                e.printStackTrace();

            }
        }

    }


    public static void findSoup() throws GameActionException {
        int x_min = -1;
        int x_max = 1;
        int y_min = -1;
        int y_max = 1;
        int radius = 8;
        MapLocation seach_center = myLoc;
        foundSoup = false;
        outerloop:
        for (int r = 1; r < radius; r++) {
            for (int b = y_min * r; b < y_max * r; b++) {
                for (int a = x_min * r; a <= x_max * r; a++) {
                    myLoc = rc.getLocation();
                    if ((a * a + b * b) < RobotType.MINER.sensorRadiusSquared) {
                        MapLocation search_location = new MapLocation(seach_center.x + a, seach_center.y + b);
                        if (myLoc.distanceSquaredTo(search_location) < RobotType.MINER.sensorRadiusSquared) {
                            if (rc.canSenseLocation(search_location) && !rc.senseFlooding(search_location)) {
                                int soup = rc.senseSoup(search_location);
                                if (soup > 0) {
                                    soupLoc = search_location;
                                    System.out.println("FOUND SOUP WITH SOUP SCAN");
                                    break outerloop;
                                }
                            }


                            if (rc.canSenseLocation(search_location) && rc.senseFlooding(search_location) && rc.senseSoup(search_location) > 0) {
                                for (Direction dir : directions) {
                                    MapLocation search_alt_location = search_location.add(dir);
                                    if (!rc.senseFlooding(search_alt_location)) {
                                        int soup = rc.senseSoup(search_location);
                                        if (soup > 0) {
                                            soupLoc = search_location;
                                            System.out.println("FOUND SOUP WITH SOUP SCAN");
                                            break outerloop;
                                        }
                                    }
                                }


                            }
                        }
                    }


                }
            }
        }
    }

    public static void scanForRefinery(){
        if (friendlyRobots.length>0){
        for (RobotInfo r:friendlyRobots){
            if (r.type==RobotType.REFINERY) {
                if ((r.location.distanceSquaredTo(myLoc)) < refineryLock.distanceSquaredTo(myLoc)) {
                    refineryLock = r.location;
                }
            }
            }
        }
    }


    public static void makeMove(Direction move_dir) throws GameActionException {
        if (rc.isReady() && rc.canMove(move_dir) && !rc.senseFlooding(myLoc.add(move_dir))) {
            trail.add(myLoc.add(move_dir));
            if (trail.size() >= 4) {
                trail.remove(0);
            }
            rc.move(move_dir);
        }
    }

    static void random_explore(Direction explore_dir) throws GameActionException {
        while (!rc.canMove(explore_dir)) {
            explore_Dir = explore_Dir.rotateRight();
        }
    }

    static Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }

    static int fullSoupScan() throws GameActionException {
        int x_min = -1;
        int x_max = 1;
        int y_min = -1;
        int y_max = 1;
        int radius = 8;
        MapLocation seach_center = myLoc;
        myLoc = rc.getLocation();
        int totalSoup = 0;
        for (int r = 1; r < radius; r++) {
            for (int b = y_min * r; b < y_max * r; b++) {
                for (int a = x_min * r; a <= x_max * r; a++) {
                    myLoc = rc.getLocation();
                    if ((a * a + b * b) < RobotType.MINER.sensorRadiusSquared) {
                        MapLocation search_location = new MapLocation(seach_center.x + a, seach_center.y + b);
                        if (myLoc.distanceSquaredTo(search_location) < RobotType.MINER.sensorRadiusSquared) {
                            if (rc.canSenseLocation(search_location) && !rc.senseFlooding(search_location)) {
                                int soup = rc.senseSoup(search_location);
                                if (soup > 0) {

                                    totalSoup = totalSoup + soup;

                                }
                            }
                        }
                    }
                }

            }
        }
        return totalSoup;
    }

    static void mother_Nearby() {
        for (RobotInfo r : friendlyRobots) {
            if (r.type == RobotType.HQ) {
                hqLoc = r.location;
                refineryLock = hqLoc;
            }
        }
    }

}

