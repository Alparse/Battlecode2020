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
    static int minerJob = 0;
    static MapLocation lastSoupLoc = null;

    enum minerState {SCANNINGSOUP, SEARCHINGSOUP, GOINGSOUP, MININGSOUP, RETURNINGSOUP, DEPOSITINGSOUP}

    static minerState myState = minerState.SCANNINGSOUP;

    static void runMiner() throws GameActionException {
        mother_Nearby();

        if (hqLoc == null) {
            Communications.getHqLocFromBlockchain();
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
                mother_Nearby();
                friendlyRobots = rc.senseNearbyRobots(-1, myTeam);
                enemyRobots = rc.senseNearbyRobots(-1, enemyTeam);
                Utility.friendlyRobotScan();
                Utility.enemyRobotScan();


                if (minerJob == 1) {
                    System.out.println("I AM A CONSTRUCTOR");
                    if (!designCenterNear && !designCenterBuilt && HQNear && myLoc.distanceSquaredTo(hqLoc) > 2) {
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

                    if (myLoc.distanceSquaredTo(hqLoc) < 13) {
                        myLoc = rc.getLocation();
                        if (myLoc.distanceSquaredTo(headQuarters) > 8) {
                            explore_Dir = myLoc.directionTo(hqLoc).rotateRight();
                        } else {
                            explore_Dir = myLoc.directionTo(headQuarters).opposite();

                        }
                    }
                    if (myLoc.distanceSquaredTo(hqLoc) >= 25) {
                        explore_Dir = myLoc.directionTo(hqLoc);
                    }
                    makeMove(explore_Dir);
                }

                if (minerJob == 0) {
                    System.out.println("I AM A MINER");

                    switch (myState) {
                        case SCANNINGSOUP:
                            System.out.println(minerState.SCANNINGSOUP);
                            if(soupLoc!=null){
                                myState=minerState.GOINGSOUP;
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
                            System.out.println(minerState.GOINGSOUP);
                            Direction move_dir = Bug1.BugGetNext(soupLoc);
                            makeMove(move_dir);
                            if (myLoc.isAdjacentTo(soupLoc)) {
                                myState = minerState.MININGSOUP;
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
                            if (rc.senseSoup(soupLoc)==0){
                                soupLoc=null;
                                findSoup();
                                myState=minerState.SCANNINGSOUP;
                            }
                            break;

                        case RETURNINGSOUP:
                            System.out.println(minerState.RETURNINGSOUP);
                            move_dir = Bug1.BugGetNext(hqLoc);
                            makeMove(move_dir);
                            if (myLoc.isAdjacentTo(hqLoc)) {
                                myState = minerState.DEPOSITINGSOUP;
                            }
                            break;

                        case DEPOSITINGSOUP:
                            System.out.println(minerState.DEPOSITINGSOUP);
                            if (rc.isReady() && rc.canDepositSoup(myLoc.directionTo(hqLoc))) {
                                rc.depositSoup(myLoc.directionTo(hqLoc), RobotType.MINER.soupLimit);
                            }
                            if (rc.getSoupCarrying() == 0) {
                                myState = minerState.SCANNINGSOUP;
                            }
                            break;
                    }
                }

            System.out.println("BYTECODE END " + Clock.getBytecodeNum());
        } catch(Exception e){
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

    static boolean mother_Nearby() {
        RobotInfo[] nearby_Friendlies = rc.senseNearbyRobots(-1, myTeam);
        for (RobotInfo r : nearby_Friendlies) {
            if (myType == RobotType.MINER) {
                if (r.type == RobotType.HQ) {
                    hqLoc = r.location;
                    headQuarters = hqLoc;
                    return true;
                }
                if (r.type == RobotType.REFINERY) {
                    hqLoc = r.location;
                    return true;
                }
            }
        }
        return false;
    }

}

