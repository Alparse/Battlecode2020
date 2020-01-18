package Generation1_1;

import battlecode.common.*;

import java.util.Arrays;
import java.util.stream.StreamSupport;


public class Miner2 extends RobotPlayer {
    static RobotController rc = RobotPlayer.rc;
    static MapLocation soupLoc = null;

    static boolean construction_worker = false;
    static boolean foundSoup = false;
    static boolean goingtoSoup = false;
    static boolean miningSoup = false;
    static boolean returningSoup = false;
    static boolean refiningSoup = false;
    static boolean designCenterBuilt=false;
    static int minerJob=0;


    static void runMiner() throws GameActionException {
        mother_Nearby();
        minerJob=Communications.getMinerJobFromBlockchain();
        if (hqLoc == null) {
            Communications.getHqLocFromBlockchain();
        }


        while (true) {
            try {
                System.out.println("BYTECODE START "+Clock.getBytecodeNum());
                myLoc = rc.getLocation();
                myHeight = rc.senseElevation(myLoc);
                mother_Nearby();
                friendlyRobots = rc.senseNearbyRobots(-1, myTeam);
                enemyRobots = rc.senseNearbyRobots(-1, enemyTeam);
                Utility.friendlyRobotScan();
                Utility.enemyRobotScan();


                if (minerJob==1) {
                    System.out.println("I AM A CONSTRUCTOR");
                    if (!designCenterNear&&!designCenterBuilt&& HQNear && myLoc.distanceSquaredTo(hqLoc) > 2) {
                        System.out.println("TESTER TRUE");
                        for (Direction dir : directions)
                            if (rc.isReady() && Utility.tryBuild(RobotType.DESIGN_SCHOOL, dir)) {
                                hqLoc = myLoc.add(dir);
                                designCenterNear = true;
                                designCenterBuilt=true;
                                minerJob=0;
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

                if (minerJob==0) {
                    System.out.println("I AM A MINER");


                    if (!foundSoup && !goingtoSoup && !miningSoup && !returningSoup && !refiningSoup) {
                        System.out.println("LOOKING FOR SOUP");
                        if (soupLoc == null) {
                            findSoup();
                        //} else {
                           // foundSoup = true;
                        }
                        if (soupLoc!=null){
                            foundSoup=true;
                        }
                        lastBuggingDirection = null;
                        bugPathState = BugPathState.NONE;
                        if (foundSoup) {
                            miningSoup = false;
                            returningSoup = false;
                            refiningSoup = false;
                            goingtoSoup = true;
                        }
                        if (!foundSoup) {
                            System.out.println("NO SOOP FOR YOU ");
                            while (!rc.canMove(explore_Dir)) {
                                explore_Dir = randomDirection();
                            }
                            makeMove(explore_Dir);
                        }
                    }
                    if (foundSoup && goingtoSoup && !miningSoup && !returningSoup && !refiningSoup) {
                        System.out.println("GOING TO  SOUP AT " + soupLoc);
                        myLoc = rc.getLocation();

                        goToClosestSoup(soupLoc);
                    }
                    if (foundSoup && !goingtoSoup && miningSoup && !returningSoup && !refiningSoup) {
                        System.out.println("MINING SOUP AT " + soupLoc);
                        mine_Soup();
                        lastBuggingDirection = null;
                        bugPathState = BugPathState.NONE;
                    }
                    if (!goingtoSoup && !miningSoup && returningSoup && !refiningSoup) {
                        int soup = fullSoupScan();
                        if (rc.getRoundNum() > 200 && soup > 200) {
                            if (!refineryNear && !HQNear) {
                                for (Direction dir : directions) {
                                    if (Utility.tryBuild(RobotType.REFINERY, dir)) {
                                        hqLoc = myLoc.add(dir);
                                        refineryNear = true;
                                        break;

                                    }
                                }
                            }
                        }
                    }
                    System.out.println("RETURNING SOUP TO " + hqLoc);
                    returnSoup(hqLoc);

                    if (!goingtoSoup && !miningSoup && !returningSoup && refiningSoup) {
                        if (!refineryNear && !HQNear) {
                            System.out.println("TRYING TO BUILD REFINERY 2");
                            for (Direction dir : directions) {
                                if (Utility.tryBuild(RobotType.REFINERY, dir)) {
                                    System.out.println("TRYING TO BUILD REFINERY 3");
                                    hqLoc = myLoc.add(dir);
                                    refineryNear = true;
                                    break;
                                }
                            }
                        }
                        System.out.println("REFINING SOUP AT " + soupLoc);
                        refine_Soup();
                    }
                }
                System.out.println("BYTECODE END "+Clock.getBytecodeNum());
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
                                    foundSoup = true;
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
                                            foundSoup = true;
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

    public static void goToClosestSoup(MapLocation souploc) throws GameActionException {
        System.out.println("LINE  1 SoupLoc  " + soupLoc);
        myLoc = rc.getLocation();
        if (myLoc.isAdjacentTo(souploc)) {
            lastBuggingDirection = null;
            bugPathState = BugPathState.NONE;
        }
        if (rc.canSenseLocation(soupLoc) && rc.senseSoup(soupLoc) == 0) {
            soupLoc = null;
            findSoup();
            foundSoup = false;
            goingtoSoup = false;
            miningSoup = false;
            returningSoup = false;
            refiningSoup = false;
            return;
        }

        if (soupLoc != null) {
            if (rc.canSenseLocation(soupLoc) && soupLoc.isAdjacentTo(myLoc) && rc.senseSoup(soupLoc) > 0) {

                foundSoup = true;
                goingtoSoup = false;
                miningSoup = true;
                returningSoup = false;
                refiningSoup = false;
                return;
            }
        }
        if (rc.canSenseLocation(soupLoc) && !(soupLoc.isAdjacentTo(myLoc)) && rc.senseSoup(soupLoc) > 0) {
            System.out.println(soupLoc);
            foundSoup = true;
            goingtoSoup = true;
            miningSoup = false;
            returningSoup = false;
            refiningSoup = false;

        }
        Direction move_dir = Bug1.BugGetNext(soupLoc);
        makeMove(move_dir);
    }


    static void mine_Soup() throws GameActionException {
        myLoc = rc.getLocation();
        System.out.println("MINING SOUP");
        if(rc.canSenseLocation(soupLoc)){
            if (rc.getSoupCarrying() == RobotType.MINER.soupLimit && rc.senseSoup(soupLoc) > 0) {
                foundSoup = true;
                goingtoSoup = false;
                miningSoup = false;
                returningSoup = true;
                refiningSoup = false;
                return;
            }
            if (rc.senseSoup(soupLoc) == 0 && rc.getSoupCarrying() < RobotType.MINER.soupLimit) {
                findSoup();
                //soupLoc = null;
                foundSoup = false;
                goingtoSoup = false;
                miningSoup = false;
                returningSoup = false;
                refiningSoup = false;
                return;
            }
            if (rc.senseSoup(soupLoc) == 0) {
                findSoup();
                //soupLoc = null;
                foundSoup = false;
                goingtoSoup = false;
                miningSoup = false;
                returningSoup = false;
                refiningSoup = false;
                return;
            }
        }


        if (rc.canMineSoup(myLoc.directionTo(soupLoc)) && !returningSoup) {
            foundSoup = true;
            goingtoSoup = false;
            miningSoup = true;
            returningSoup = false;
            refiningSoup = false;
            if (rc.isReady()) {
                rc.mineSoup(myLoc.directionTo(soupLoc));
            }
            return;
        }

    }

    static void returnSoup(MapLocation hqloc) throws GameActionException {
        if (myLoc.isAdjacentTo(hqLoc)) {
            goingtoSoup = false;
            miningSoup = false;
            returningSoup = false;
            refiningSoup = true;
            bugPathState = BugPathState.NONE;
            return;
        }
        //Direction move_dir = myLoc.directionTo(hqLoc);
        System.out.println("BUG MOVE DIR start");
        Direction move_dir = Bug1.BugGetNext(hqLoc);
        System.out.println("BUG MOVE DIR " + move_dir);
        makeMove(move_dir);
    }

    static void refine_Soup() throws GameActionException {
        myLoc = rc.getLocation();
        System.out.println("REFINING SOUP" + rc.getSoupCarrying());
        if (rc.isReady() && rc.canDepositSoup(myLoc.directionTo(hqLoc))) {
            rc.depositSoup(myLoc.directionTo(hqLoc), RobotType.MINER.soupLimit);
            System.out.println("TRYING TO DEPOSIT SOUP");
        }
        if (rc.getSoupCarrying() == 0) {
            //soupLoc = null;
            foundSoup = false;
            goingtoSoup = false;
            miningSoup = false;
            returningSoup = false;
            refiningSoup = false;
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




