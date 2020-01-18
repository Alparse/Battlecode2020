package Generation1_1;

import battlecode.common.*;

import java.util.Arrays;


public class Miner2 extends RobotPlayer {
    static RobotController rc = RobotPlayer.rc;
    static MapLocation soupLoc = null;

    static boolean construction_worker = false;
    static boolean foundSoup = false;
    static boolean goingtoSoup = false;
    static boolean miningSoup = false;
    static boolean returningSoup = false;
    static boolean refiningSoup = false;


    static void runMiner() throws GameActionException {
        mother_Nearby();
        if (rc.getRoundNum() > 25 && !designSchool_Nearby() && rc.getRoundNum() < 100) {
            construction_worker = true;
        }

        while (true) {
            try {
                myLoc = rc.getLocation();
                myHeight = rc.senseElevation(myLoc);
                System.out.println("CONST WORKER " + construction_worker);
                mother_Nearby();

                if (construction_worker) {
                    System.out.println(HQ_Nearby());
                    System.out.println(designSchool_Nearby());
                    System.out.println(fullfillmentCenter_Nearby());
                    if (designSchool_Nearby()) {
                        construction_worker = false;
                    }

                    if (!designSchool_Nearby() && HQ_Nearby() && myLoc.distanceSquaredTo(hqLoc) > 2) {
                        System.out.println("TESTER TRUE");
                        for (Direction dir : directions)
                            if (rc.isReady() && Utility.tryBuild(RobotType.DESIGN_SCHOOL, dir)) {
                                hqLoc = myLoc.add(dir);
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

                if (!construction_worker) {
                    Communications.getHqLocFromBlockchain();
                    if (rc.getRoundNum() > 500) {
                        for (Direction dir : directions)
                            if (myLoc.distanceSquaredTo(headQuarters) > 30) {
                                int soup = fullSoupScan();
                                if (!refinery_Nearby() && soup > 100) {
                                    if (Utility.tryBuild(RobotType.VAPORATOR, dir)) {
                                        hqLoc = myLoc.add(dir);
                                    }
                                }
                            }
                    }

                    if (!foundSoup && !goingtoSoup && !miningSoup && !returningSoup && !refiningSoup) {
                        System.out.println("LOOKING FOR SOUP");
                        if (soupLoc == null) {
                            findSoup();
                        } else {
                            foundSoup = true;
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
                        System.out.println("TRYING TO BUILD REFINERY 1");
                        int soup = fullSoupScan();
                        System.out.println("TRYING TO BUILD REFINERY 1.12" + soup);
                        System.out.println("TRYING TO BUILD REFINERY 1.1");
                        if (rc.getRoundNum() > 200 && soup > 100) {
                            System.out.println("TRYING TO BUILD REFINERY 1.3");
                            if (!refinery_Nearby() && !HQ_Nearby()) {
                                System.out.println("TRYING TO BUILD REFINERY 2");
                                for (Direction dir : directions) {
                                    if (Utility.tryBuild(RobotType.REFINERY, dir)) {
                                        System.out.println("TRYING TO BUILD REFINERY 3");
                                        hqLoc = myLoc.add(dir);

                                    }
                                }
                            }
                        }
                    }
                    System.out.println("RETURNING SOUP TO " + hqLoc);
                    returnSoup(hqLoc);

                    if (!goingtoSoup && !miningSoup && !returningSoup && refiningSoup) {
                        System.out.println("REFINING SOUP AT " + soupLoc);
                        refine_Soup();
                        if (!netGun_Nearby()) {
                            if (rc.getTeamSoup() > 200) {
                                for (Direction dir : directions)
                                    if (Utility.tryBuild(RobotType.NET_GUN, dir)) {
                                    }
                            }
                        }
                    }
                }
                Clock.yield();
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
        if (rc.canSenseLocation(soupLoc) && rc.senseSoup(soupLoc) == 0) {
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
        myLoc=rc.getLocation();
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

    static boolean netGun_Nearby() {
        RobotInfo[] nearby_Friendlies = rc.senseNearbyRobots(-1, myTeam);
        for (RobotInfo r : nearby_Friendlies) {
            if (myType == RobotType.MINER) {
                if (r.type == RobotType.NET_GUN) {
                    return true;
                }
                if (r.type == RobotType.HQ) {
                    return true;

                }
            }
        }
        return false;
    }

    static boolean designSchool_Nearby() {
        RobotInfo[] nearby_Friendlies = rc.senseNearbyRobots(-1, myTeam);
        for (RobotInfo r : nearby_Friendlies) {
            if (r.type == RobotType.DESIGN_SCHOOL) {
                return true;


            }
        }
        return false;
    }

    static boolean fullfillmentCenter_Nearby() {
        RobotInfo[] nearby_Friendlies = rc.senseNearbyRobots(-1, myTeam);
        for (RobotInfo r : nearby_Friendlies) {
            if (myType == RobotType.MINER) {
                if (r.type == RobotType.FULFILLMENT_CENTER) {
                    return true;
                }

            }
        }
        return false;
    }

    static boolean HQ_Nearby() {
        RobotInfo[] nearby_Friendlies = rc.senseNearbyRobots(-1, myTeam);
        for (RobotInfo r : nearby_Friendlies) {
            if (myType == RobotType.MINER) {
                if (r.type == RobotType.HQ) {
                    return true;
                }

            }
        }
        return false;
    }

    static boolean refinery_Nearby() {
        RobotInfo[] nearby_Friendlies = rc.senseNearbyRobots(-1, myTeam);
        for (RobotInfo r : nearby_Friendlies) {
            if (myType == RobotType.MINER) {
                if (r.type == RobotType.REFINERY) {
                    return true;
                }

            }
        }
        return false;
    }

    static void tryBlockchain() throws GameActionException {
        int[] message = new int[10];
        message = new int[]{1, 2, 3, 4, 5, 6, 7};
        if (rc.canSubmitTransaction(message, 1))
            rc.submitTransaction(message, 1);

        Transaction[] test = rc.getBlock(rc.getRoundNum() - 1);
        System.out.println(Arrays.toString(test));


    }
}




