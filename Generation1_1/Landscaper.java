package Generation1_1;

import battlecode.common.*;


public class Landscaper extends RobotPlayer {
    static RobotController rc = RobotPlayer.rc;
    static MapLocation soupLoc = null;
    static boolean levee_builder = true;
    static boolean grave_digger = false;
    static boolean at_spot = false;


    static void runLandscaper() throws GameActionException {
        System.out.println("BYTECODE START "+Clock.getBytecodeNum());
        myLoc = rc.getLocation();
        myHeight = rc.senseElevation(myLoc);
        mother_Nearby();
        friendlyRobots = rc.senseNearbyRobots(-1, myTeam);
        enemyRobots = rc.senseNearbyRobots(-1, enemyTeam);
        Utility.friendlyRobotScan();
        Utility.enemyRobotScan();

        if (hqLoc == null) {
            hqLoc = Communications.getHqLocFromBlockchain();
        }

        System.out.println("HQ LOC " + hqLoc);
        Direction dig_dirt_dir = null;
        if (myLoc.isAdjacentTo(hqLoc)) {
            System.out.println("AT SPOT");
            if (enemyLandscaperNear||rc.getRoundNum()>200) {

                dig_dirt_dir = hqLoc.directionTo(myLoc);
                if (buildingBeingBuried(hqLoc)) {
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
        System.out.println("BYTECODE END "+Clock.getBytecodeNum());

    }

    public static void makeMove(Direction move_dir) throws GameActionException {
        if (rc.isReady() && rc.canMove(move_dir)) {
            rc.move(move_dir);
        }
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


    static boolean buildingBeingBuried(MapLocation building_loc) throws GameActionException {
        RobotInfo building = rc.senseRobotAtLocation(building_loc);
        if (building.dirtCarrying > 0) {
            return true;
        }
        return false;
    }

}
