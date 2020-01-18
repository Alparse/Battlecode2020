package Generation1_1;

import battlecode.common.*;


public class Landscaper extends RobotPlayer {
    static RobotController rc = RobotPlayer.rc;
    static MapLocation soupLoc = null;
    static boolean levee_builder = true;
    static boolean grave_digger = false;
    static boolean at_spot = false;


    static void runLandscaper() throws GameActionException {
        //sense_Mother_HQ();
        myLoc = rc.getLocation();
        //myHeight = rc.senseElevation(myLoc);
        mother_Nearby();
        if (hqLoc == null) {
            hqLoc = Communications.getHqLocFromBlockchain();
        }
        System.out.println("HQ LOC " + hqLoc);
        Direction dig_dirt_dir = null;
        if (myLoc.isAdjacentTo(hqLoc)) {
            System.out.println("AT SPOT");
            if (enemyLandscaperScan()!=null||rc.getRoundNum()>200) {

                dig_dirt_dir = hqLoc.directionTo(myLoc);
                if (buildingBeingBuried(hqLoc)) {
                    System.out.println("HEEELLPPP MEEEEEEEE IM BEING BURIED ALIVEEEE");
                    dig_dirt_dir = myLoc.directionTo(hqLoc);
                }
                if (rc.canDigDirt(dig_dirt_dir)) {
                    if (rc.isReady()) {
                        rc.digDirt(dig_dirt_dir);
                    }
                } else {
                    for (Direction d : directions) {
                        if (rc.canDigDirt(dig_dirt_dir)) {
                            if (rc.isReady()) {
                                rc.digDirt(dig_dirt_dir);
                            }
                        }
                    }

                }
            }
            if (rc.getDirtCarrying() == RobotType.LANDSCAPER.dirtLimit) {
                Direction deposit_dir = dirtScan();
                if (deposit_dir != null) {
                    if (rc.canDepositDirt(deposit_dir)) {
                        if (rc.isReady()) {
                            rc.depositDirt(deposit_dir);
                        }
                    }
                }
                if (rc.canDepositDirt(Direction.CENTER)) {
                    if (rc.isReady()) {
                        rc.depositDirt(Direction.CENTER);
                    }

                }
            }
        }
        if (levee_builder && !myLoc.isAdjacentTo(hqLoc)) {

            System.out.println("LEVEE BUILDER" + hqLoc);
            System.out.println("BUG MOVE DIR start");
            Direction move_dir = Bug1.BugGetNext(hqLoc);
            System.out.println("BUG MOVE DIR " + move_dir);

            makeMove(move_dir);

        }
        Clock.yield();

    }

    public static void makeMove(Direction move_dir) throws GameActionException {
        if (rc.isReady() && rc.canMove(move_dir)) {
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

    static Direction dirtScan() throws GameActionException {

        myLoc = rc.getLocation();
        for (Direction dir : directions)
            if (!myLoc.add(dir).equals(hqLoc) && myLoc.add(dir).isAdjacentTo(hqLoc)) {
                int height_dif = rc.senseElevation(myLoc) - rc.senseElevation(myLoc.add(dir));
                System.out.println("HQ LOC " + hqLoc);
                if (height_dif > RobotType.LANDSCAPER.dirtLimit) {
                    System.out.println("DUMP LOCATION " + dir);
                    return dir;
                }
            }

        return null;
    }

    static boolean mother_Nearby() {
        RobotInfo[] nearby_Friendlies = rc.senseNearbyRobots(-1, myTeam);
        for (RobotInfo r : nearby_Friendlies) {
            if (myType == RobotType.MINER) {
                if (r.type == RobotType.HQ) {
                    hqLoc = r.location;
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
            if (myType == RobotType.MINER) {
                if (r.type == RobotType.DESIGN_SCHOOL) {
                    return true;
                }

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

    static int landscapersInRange() {
        RobotInfo[] nearby_Friendlies = rc.senseNearbyRobots(-1, myTeam);
        int landscapers = 0;
        for (RobotInfo r : nearby_Friendlies) {
            if (r.type == RobotType.LANDSCAPER) {
                landscapers = landscapers + 1;
            }
        }
        return landscapers;
    }

    static RobotInfo enemyLandscaperScan() throws GameActionException {
        enemyRobots = rc.senseNearbyRobots(-1, enemyTeam);
        int distance = 999;
        int r_distance = 1000;
        RobotInfo target = null;
        for (RobotInfo r : enemyRobots) {
            if (r.getType() == RobotType.LANDSCAPER) {
                r_distance = myLoc.distanceSquaredTo(r.location);
                if (r_distance < distance) {
                    target = r;
                }
            }

        }
        return target;
    }

    static RobotInfo enemyHQScan() throws GameActionException {
        enemyRobots = rc.senseNearbyRobots(-1, enemyTeam);
        for (RobotInfo r : enemyRobots) {
            if (r.getType() == RobotType.HQ) {
                return r;
            }
        }
        return null;
    }

    static boolean buildingBeingBuried(MapLocation building_loc) throws GameActionException {
        RobotInfo building = rc.senseRobotAtLocation(building_loc);
        if (building.dirtCarrying > 0) {
            return true;
        }
        return false;
    }

}
