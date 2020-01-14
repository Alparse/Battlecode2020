package Generation1_1;

import battlecode.common.*;


public class Miner2 extends RobotPlayer {
    static RobotController rc = RobotPlayer.rc;
    static MapLocation soupLoc = null;


    static enum MinerState {NONE, FOUNDSOUP, GOINGTOSOUP, MININGSOUP, RETURNINGSOUP, REFININGSOUP}

    static boolean foundSoup = false;
    static boolean goingtoSoup = false;
    static boolean miningSoup = false;
    static boolean returningSoup = false;
    static boolean refiningSoup = false;


    static void runMiner() throws GameActionException {
        myLoc = rc.getLocation();
        myHeight = rc.senseElevation(myLoc);
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
                while(!rc.canMove(explore_Dir)){
                    explore_Dir=randomDirection();
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
            System.out.println("RETURNING SOUP TO " + hqLoc);
            returnSoup(hqLoc);
        }
        if (!goingtoSoup && !miningSoup && !returningSoup && refiningSoup) {
            System.out.println("REFINING SOUP AT " + soupLoc);
            refine_Soup();
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
                            if (rc.canSenseLocation(search_location)&&!rc.senseFlooding(search_location)) {
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
        System.out.println("BUG MOVE DIR "+move_dir);
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
        if (rc.isReady() && rc.canMove(move_dir)) {
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
}

