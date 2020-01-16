package Generation1_1;

import battlecode.common.*;


public class Landscaper extends RobotPlayer {
    static RobotController rc = RobotPlayer.rc;
    static MapLocation soupLoc = null;
    static boolean levee_builder = true;
    static boolean grave_digger = false;


    static void runLandscaper() throws GameActionException {
        //sense_Mother_HQ();
        myLoc = rc.getLocation();
        //myHeight = rc.senseElevation(myLoc);
        mother_Nearby();
        Direction dig_dirt_dir = null;
        if (myLoc.isAdjacentTo(hqLoc)) {
            System.out.println("AT SPOT");
            dig_dirt_dir = hqLoc.directionTo(myLoc);
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
        int x_min = -1;
        int x_max = 1;
        int y_min = -1;
        int y_max = 1;
        int radius = 1;
        int r=1;
        MapLocation seach_center = hqLoc;
        int totalSoup = 0;
            for (int b = y_min * r; b < y_max * r; b++) {
                for (int a = x_min * r; a <= x_max * r; a++) {
                    myLoc = rc.getLocation();
                    if ((a * a + b * b) < RobotType.MINER.sensorRadiusSquared) {
                        MapLocation search_location = new MapLocation(seach_center.x + a, seach_center.y + b);
                        if(!search_location.equals(hqLoc)){
                            System.out.println("SEARCLOC "+search_location+" HQ "+hqLoc);
                            RobotInfo target_robot = rc.senseRobotAtLocation(search_location);
                            int height_dif = rc.senseElevation(myLoc) - rc.senseElevation(search_location);
                            System.out.println("HQ LOC "+hqLoc);
                            if (height_dif > RobotType.LANDSCAPER.dirtLimit && search_location.isAdjacentTo(hqLoc)&&search_location.isAdjacentTo(myLoc)) {
                                System.out.println("DUMP LOCATION " + search_location);
                                return myLoc.directionTo(search_location);
                            }


                    }
                }
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


}
