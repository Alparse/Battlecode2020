package Generation3_1;

import battlecode.common.*;

import java.util.ArrayList;


public class Utility extends RobotPlayer {
    static RobotController rc = RobotPlayer.rc;

    static void test_speech() {

        System.out.println("TEST SPEECH");
        rc.getTeam();

    }

    static boolean tryBuild(RobotType type, Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        } else return false;
    }

    static void buildBuilding(RobotType building) throws GameActionException {
        for (Direction dir : directions)
            Utility.tryBuild(building, dir);
    }


    static void set_teams() {
        myTeam = rc.getTeam();
        if (myTeam == Team.A) {
            enemyTeam = Team.B;
        }
        if (myTeam == Team.B) {
            enemyTeam = Team.B;
        }
    }

    static void friendlyRobotScan() {
        HQNear = false;
        designCenterNear = false;
        fulfillmentCenterNear = false;
        refineryNear = false;
        netgunNear = false;
        vaporatorNear = false;
        for (RobotInfo r : friendlyRobots) {
            if (r.type == RobotType.HQ) {
                HQNear = true;
            }
            if (r.type == RobotType.DESIGN_SCHOOL) {
                designCenterNear = true;
            }
            if (r.type == RobotType.FULFILLMENT_CENTER) {
                fulfillmentCenterNear = true;
            }
            if (r.type == RobotType.REFINERY) {
                refineryNear = true;
            }
            if (r.type == RobotType.NET_GUN) {
                netgunNear = true;
            }
            if (r.type == RobotType.VAPORATOR) {
                vaporatorNear = true;
            }
        }

    }

    static void enemyRobotScan() {
        enemiesNear = false;
        enemyLandscaperNear = false;
        if (enemyRobots.length > 0) {
            enemiesNear = true;
        }
        for (RobotInfo r : enemyRobots) {
            if (r.type == RobotType.LANDSCAPER) {
                enemyLandscaperNear = true;
                break;
            }
        }
    }

    static boolean isWalledOff(MapLocation targetLocation) throws GameActionException {

        MapLocation search_location1 = new MapLocation(targetLocation.x + -1, targetLocation.y + 1);
        MapLocation search_location2 = new MapLocation(targetLocation.x + -1, targetLocation.y + 0);
        MapLocation search_location3 = new MapLocation(targetLocation.x + -1, targetLocation.y - 1);
        MapLocation search_location4 = new MapLocation(targetLocation.x + 0, targetLocation.y + 1);
        MapLocation search_location5 = new MapLocation(targetLocation.x + 0, targetLocation.y - 1);
        MapLocation search_location6 = new MapLocation(targetLocation.x + 1, targetLocation.y + 1);
        MapLocation search_location7 = new MapLocation(targetLocation.x + 1, targetLocation.y + 0);
        MapLocation search_location8 = new MapLocation(targetLocation.x + 1, targetLocation.y - 1);

        if (isNotWall(search_location1) || isNotWall(search_location2) || isNotWall(search_location3) || isNotWall(search_location4) || isNotWall(search_location5) || isNotWall(search_location6) || isNotWall(search_location7) || isNotWall(search_location8)) {
            return false;
        }
        return true;
    }


    public static boolean isNotWall(MapLocation searchLocation) throws GameActionException {

        if (!rc.canSenseLocation(searchLocation)) {
            return false;
        }
        int destinationHeight = rc.senseElevation(searchLocation);
        boolean notFlooded = !rc.senseFlooding(searchLocation);
        boolean notTooHigh = !(Math.abs(destinationHeight - myHeight) > 6);
        boolean notOccupied = (rc.senseRobotAtLocation(searchLocation) == null);
        boolean onTheMap = rc.onTheMap(searchLocation);
        return notFlooded && notTooHigh && notOccupied && onTheMap;

    }


    static ArrayList<MapLocation> digLocations(MapLocation targetLocation) throws GameActionException {
        ArrayList<MapLocation> largeWall = new ArrayList<>();

        MapLocation search_location1 = new MapLocation(targetLocation.x + -4, targetLocation.y + 1);
        MapLocation search_location2 = new MapLocation(targetLocation.x + -3, targetLocation.y - 2);
        MapLocation search_location3 = new MapLocation(targetLocation.x + -1, targetLocation.y - 4);
        MapLocation search_location4 = new MapLocation(targetLocation.x + 2, targetLocation.y - 3);
        MapLocation search_location5 = new MapLocation(targetLocation.x + 4, targetLocation.y - 1);
        MapLocation search_location6 = new MapLocation(targetLocation.x + 3, targetLocation.y + 2);
        MapLocation search_location7 = new MapLocation(targetLocation.x + 1, targetLocation.y + 4);
        MapLocation search_location8 = new MapLocation(targetLocation.x + -2, targetLocation.y + 3);
        MapLocation search_location9 = new MapLocation(targetLocation.x + -2, targetLocation.y + 0);
        MapLocation search_location10 = new MapLocation(targetLocation.x + 0, targetLocation.y + 2);
        MapLocation search_location11 = new MapLocation(targetLocation.x + 0, targetLocation.y - 2);
        MapLocation search_location12 = new MapLocation(targetLocation.x + 2, targetLocation.y + 0);
        if (rc.onTheMap(search_location1)) {
            largeWall.add(search_location1);
        }
        if (rc.onTheMap(search_location2)) {
            largeWall.add(search_location2);
        }
        if (rc.onTheMap(search_location3)) {
            largeWall.add(search_location3);
        }
        if (rc.onTheMap(search_location4)) {
            largeWall.add(search_location4);
        }
        if (rc.onTheMap(search_location5)) {
            largeWall.add(search_location5);
        }
        if (rc.onTheMap(search_location6)) {
            largeWall.add(search_location6);
        }
        if (rc.onTheMap(search_location7)) {
            largeWall.add(search_location7);
        }
        if (rc.onTheMap(search_location8)) {
            largeWall.add(search_location8);
        }
        if (rc.onTheMap(search_location9)) {
            largeWall.add(search_location9);
        }
        if (rc.onTheMap(search_location10)) {
            largeWall.add(search_location10);
        }
        if (rc.onTheMap(search_location11)) {
            largeWall.add(search_location11);
        }
        if (rc.onTheMap(search_location12)) {
            largeWall.add(search_location12);
        }
        return largeWall;
    }

    static ArrayList<MapLocation> buildLocations(MapLocation targetLocation) throws GameActionException {
        ArrayList<MapLocation> largeWall = new ArrayList<>();

        MapLocation search_location1 = new MapLocation(targetLocation.x + 1, targetLocation.y + 2);
        MapLocation search_location2 = new MapLocation(targetLocation.x + 2, targetLocation.y + -1);
        MapLocation search_location3 = new MapLocation(targetLocation.x + -1, targetLocation.y - 2);
        MapLocation search_location4 = new MapLocation(targetLocation.x + -2, targetLocation.y + 1);

        if (rc.onTheMap(search_location1)) {
            largeWall.add(search_location1);
        }
        if (rc.onTheMap(search_location2)) {
            largeWall.add(search_location2);
        }
        if (rc.onTheMap(search_location3)) {
            largeWall.add(search_location3);
        }
        if (rc.onTheMap(search_location4)) {
            largeWall.add(search_location4);
        }
        return largeWall;
    }

    static ArrayList<MapLocation> innerWallArray(MapLocation targetLocation) {
        ArrayList<MapLocation> largeWall = new ArrayList<>();
        MapLocation search_location1 = new MapLocation(targetLocation.x - 1, targetLocation.y - 1);
        MapLocation search_location2 = new MapLocation(targetLocation.x - 1, targetLocation.y + 0);
        MapLocation search_location3 = new MapLocation(targetLocation.x - 1, targetLocation.y + 1);
        MapLocation search_location4 = new MapLocation(targetLocation.x + 0, targetLocation.y + 1);
        MapLocation search_location5 = new MapLocation(targetLocation.x + 1, targetLocation.y + 1);
        MapLocation search_location6 = new MapLocation(targetLocation.x + 1, targetLocation.y + 0);
        MapLocation search_location7 = new MapLocation(targetLocation.x + 1, targetLocation.y - 1);
        MapLocation search_location8 = new MapLocation(targetLocation.x + 0, targetLocation.y - 1);

        if (rc.onTheMap(search_location1)) {
            largeWall.add(search_location1);
        }
        if (rc.onTheMap(search_location2)) {
            largeWall.add(search_location2);
        }
        if (rc.onTheMap(search_location3)) {
            largeWall.add(search_location3);
        }
        if (rc.onTheMap(search_location4)) {
            largeWall.add(search_location4);
        }
        if (rc.onTheMap(search_location5)) {
            largeWall.add(search_location5);
        }
        if (rc.onTheMap(search_location6)) {
            largeWall.add(search_location6);
        }
        if (rc.onTheMap(search_location7)) {
            largeWall.add(search_location7);
        }
        if (rc.onTheMap(search_location8)) {
            largeWall.add(search_location8);
        }
        return largeWall;
    }


    static ArrayList<MapLocation> outerWallArray(MapLocation targetLocation) {
        ArrayList<MapLocation> largeWall = new ArrayList<>();
        MapLocation search_location1 = new MapLocation(targetLocation.x + -3, targetLocation.y + 2);
        MapLocation search_location2 = new MapLocation(targetLocation.x + -3, targetLocation.y + 1);
        MapLocation search_location3 = new MapLocation(targetLocation.x + -3, targetLocation.y - 0);
        MapLocation search_location4 = new MapLocation(targetLocation.x + -3, targetLocation.y + -1);
        MapLocation search_location5 = new MapLocation(targetLocation.x + -2, targetLocation.y - 1);
        MapLocation search_location6 = new MapLocation(targetLocation.x - 2, targetLocation.y - 2);
        MapLocation search_location7 = new MapLocation(targetLocation.x - 2, targetLocation.y - 3);
        MapLocation search_location8 = new MapLocation(targetLocation.x - 1, targetLocation.y - 3);

        MapLocation search_location9 = new MapLocation(targetLocation.x - 0, targetLocation.y - 3);
        MapLocation search_location10 = new MapLocation(targetLocation.x + 1, targetLocation.y - 3);
        MapLocation search_location11 = new MapLocation(targetLocation.x + 1, targetLocation.y - 2);
        MapLocation search_location12 = new MapLocation(targetLocation.x + 2, targetLocation.y - 2);
        MapLocation search_location13 = new MapLocation(targetLocation.x + 3, targetLocation.y - 2);
        MapLocation search_location14 = new MapLocation(targetLocation.x + 3, targetLocation.y - 1);
        MapLocation search_location15 = new MapLocation(targetLocation.x + 3, targetLocation.y - 0);
        MapLocation search_location16 = new MapLocation(targetLocation.x + 3, targetLocation.y + 1);

        MapLocation search_location17 = new MapLocation(targetLocation.x + 2, targetLocation.y + 1);
        MapLocation search_location18 = new MapLocation(targetLocation.x + 2, targetLocation.y + 2);
        MapLocation search_location19 = new MapLocation(targetLocation.x + 2, targetLocation.y + 3);
        MapLocation search_location20 = new MapLocation(targetLocation.x + 1, targetLocation.y + 3);
        MapLocation search_location21 = new MapLocation(targetLocation.x + 0, targetLocation.y + 3);
        MapLocation search_location22 = new MapLocation(targetLocation.x - 1, targetLocation.y + 3);
        MapLocation search_location23 = new MapLocation(targetLocation.x - 1, targetLocation.y + 2);
        MapLocation search_location24 = new MapLocation(targetLocation.x - 2, targetLocation.y + 2);


            largeWall.add(search_location1);


            largeWall.add(search_location2);


            largeWall.add(search_location3);


            largeWall.add(search_location4);


            largeWall.add(search_location5);


            largeWall.add(search_location6);


            largeWall.add(search_location7);


            largeWall.add(search_location8);


            largeWall.add(search_location9);


            largeWall.add(search_location10);


            largeWall.add(search_location11);


            largeWall.add(search_location12);


            largeWall.add(search_location13);


            largeWall.add(search_location14);


            largeWall.add(search_location15);


            largeWall.add(search_location16);


            largeWall.add(search_location17);


            largeWall.add(search_location18);


            largeWall.add(search_location19);


            largeWall.add(search_location20);


            largeWall.add(search_location21);


            largeWall.add(search_location22);


            largeWall.add(search_location23);

            largeWall.add(search_location24);


        return largeWall;
    }


    static boolean isWalledOffLarge(MapLocation targetLocation) throws GameActionException {

        MapLocation search_location1 = new MapLocation(targetLocation.x + -2, targetLocation.y + 2);
        MapLocation search_location2 = new MapLocation(targetLocation.x + -2, targetLocation.y + 1);
        MapLocation search_location3 = new MapLocation(targetLocation.x + -2, targetLocation.y + 0);
        MapLocation search_location4 = new MapLocation(targetLocation.x + -2, targetLocation.y - 1);
        MapLocation search_location5 = new MapLocation(targetLocation.x + -2, targetLocation.y - 2);
        MapLocation search_location6 = new MapLocation(targetLocation.x + -1, targetLocation.y - 2);
        MapLocation search_location7 = new MapLocation(targetLocation.x + 0, targetLocation.y - 2);
        MapLocation search_location8 = new MapLocation(targetLocation.x + 1, targetLocation.y - 2);
        MapLocation search_location9 = new MapLocation(targetLocation.x + 2, targetLocation.y - 2);
        MapLocation search_location10 = new MapLocation(targetLocation.x + 2, targetLocation.y - 1);
        MapLocation search_location11 = new MapLocation(targetLocation.x + 2, targetLocation.y + 0);
        MapLocation search_location12 = new MapLocation(targetLocation.x + 2, targetLocation.y + 1);
        MapLocation search_location13 = new MapLocation(targetLocation.x + 2, targetLocation.y + 2);
        MapLocation search_location14 = new MapLocation(targetLocation.x + 1, targetLocation.y + 2);
        MapLocation search_location15 = new MapLocation(targetLocation.x + 0, targetLocation.y + 2);
        MapLocation search_location16 = new MapLocation(targetLocation.x - 1, targetLocation.y + 2);

        if (isNotWall(search_location1) || isNotWall(search_location2) || isNotWall(search_location3) || isNotWall(search_location4) || isNotWall(search_location5) || isNotWall(search_location6) || isNotWall(search_location7) ||
                isNotWall(search_location8) || isNotWall(search_location9) || isNotWall(search_location10) || isNotWall(search_location11) || isNotWall(search_location12) || isNotWall(search_location13) || isNotWall(search_location14) || isNotWall(search_location15) || isNotWall(search_location16)) {
            return false;
        }
        return true;
    }

    static boolean isOnOuterWall(MapLocation targetLocation, MapLocation object) {
        MapLocation search_location1 = new MapLocation(targetLocation.x + -2, targetLocation.y + 2);
        MapLocation search_location2 = new MapLocation(targetLocation.x + -2, targetLocation.y + 1);
        MapLocation search_location3 = new MapLocation(targetLocation.x + -2, targetLocation.y + 0);
        MapLocation search_location4 = new MapLocation(targetLocation.x + -2, targetLocation.y - 1);
        MapLocation search_location5 = new MapLocation(targetLocation.x + -2, targetLocation.y - 2);
        MapLocation search_location6 = new MapLocation(targetLocation.x + -1, targetLocation.y - 2);
        MapLocation search_location7 = new MapLocation(targetLocation.x + 0, targetLocation.y - 2);
        MapLocation search_location8 = new MapLocation(targetLocation.x + 1, targetLocation.y - 2);
        MapLocation search_location9 = new MapLocation(targetLocation.x + 2, targetLocation.y - 2);
        MapLocation search_location10 = new MapLocation(targetLocation.x + 2, targetLocation.y - 1);
        MapLocation search_location11 = new MapLocation(targetLocation.x + 2, targetLocation.y + 0);
        MapLocation search_location12 = new MapLocation(targetLocation.x + 2, targetLocation.y + 1);
        MapLocation search_location13 = new MapLocation(targetLocation.x + 2, targetLocation.y + 2);
        MapLocation search_location14 = new MapLocation(targetLocation.x + 1, targetLocation.y + 2);
        MapLocation search_location15 = new MapLocation(targetLocation.x + 0, targetLocation.y + 2);
        MapLocation search_location16 = new MapLocation(targetLocation.x - 1, targetLocation.y + 2);
        if (object.equals(search_location1) || object.equals(search_location2) || object.equals(search_location3) || object.equals(search_location4) || object.equals(search_location5) || object.equals(search_location6) || object.equals(search_location7) || object.equals(search_location8) || object.equals(search_location9) || object.equals(search_location10) || object.equals(search_location11) || object.equals(search_location12) || object.equals(search_location13) || object.equals(search_location14) || object.equals(search_location15) || object.equals(search_location16)) {
            return true;
        }
        return false;

    }

    static boolean isOnOuterWallPost(MapLocation targetLocation, MapLocation object) {
        MapLocation search_location1 = new MapLocation(targetLocation.x + -2, targetLocation.y + 2);
        MapLocation search_location2 = new MapLocation(targetLocation.x + -1, targetLocation.y + 2);
        MapLocation search_location3 = new MapLocation(targetLocation.x + 2, targetLocation.y + 2);
        MapLocation search_location4 = new MapLocation(targetLocation.x + 2, targetLocation.y + 1);
        MapLocation search_location5 = new MapLocation(targetLocation.x + -2, targetLocation.y - 1);
        MapLocation search_location6 = new MapLocation(targetLocation.x + -2, targetLocation.y - 2);
        MapLocation search_location7 = new MapLocation(targetLocation.x + 1, targetLocation.y - 2);
        MapLocation search_location8 = new MapLocation(targetLocation.x + 2, targetLocation.y - 2);
        if (object.equals(search_location1) || object.equals(search_location2) || object.equals(search_location3) || object.equals(search_location4) || object.equals(search_location5) || object.equals(search_location6) || object.equals(search_location7) || object.equals(search_location8)) {
            return true;
        }
        return false;

    }

    static boolean isDigLocation(MapLocation targetLocation, MapLocation object) {
        MapLocation search_location1 = new MapLocation(targetLocation.x + -3, targetLocation.y + 3);
        MapLocation search_location2 = new MapLocation(targetLocation.x + -3, targetLocation.y + -3);
        MapLocation search_location3 = new MapLocation(targetLocation.x + 0, targetLocation.y + -4);
        MapLocation search_location4 = new MapLocation(targetLocation.x + -4, targetLocation.y + -0);
        MapLocation search_location5 = new MapLocation(targetLocation.x + 4, targetLocation.y + 0);
        MapLocation search_location6 = new MapLocation(targetLocation.x + 0, targetLocation.y + 4);
        //MapLocation search_location3 = new MapLocation(targetLocation.x + -2, targetLocation.y + 0);
        //MapLocation search_location4 = new MapLocation(targetLocation.x + 0, targetLocation.y + 2);
        // MapLocation search_location5 = new MapLocation(targetLocation.x + 0, targetLocation.y - 2);
        //MapLocation search_location6 = new MapLocation(targetLocation.x + 2, targetLocation.y + 0);
        MapLocation search_location7 = new MapLocation(targetLocation.x + 3, targetLocation.y + 3);
        MapLocation search_location8 = new MapLocation(targetLocation.x + 3, targetLocation.y + -3);
        if (object.equals(search_location1) || object.equals(search_location2) || object.equals(search_location3) || object.equals(search_location4) || object.equals(search_location5) || object.equals(search_location6) || object.equals(search_location7) || object.equals(search_location8)) {
            return true;
        }
        return false;

    }

    static boolean isKeepBuildLocation(MapLocation targetLocation, MapLocation object) {
        MapLocation search_location1 = new MapLocation(targetLocation.x + 1, targetLocation.y + 2);
        MapLocation search_location2 = new MapLocation(targetLocation.x + 2, targetLocation.y + -1);
        MapLocation search_location3 = new MapLocation(targetLocation.x + -1, targetLocation.y - 2);
        MapLocation search_location4 = new MapLocation(targetLocation.x + -2, targetLocation.y + 1);

        if (object.equals(search_location1) || object.equals(search_location2) || object.equals(search_location3) || object.equals(search_location4)) {
            return true;
        }
        return false;

    }


    static boolean isOnOInnerWall(MapLocation targetLocation, MapLocation object) {
        MapLocation search_location1 = new MapLocation(targetLocation.x + -1, targetLocation.y + 1);
        MapLocation search_location2 = new MapLocation(targetLocation.x + -1, targetLocation.y + 0);
        MapLocation search_location3 = new MapLocation(targetLocation.x + -1, targetLocation.y - 1);
        MapLocation search_location4 = new MapLocation(targetLocation.x + 0, targetLocation.y + 1);
        MapLocation search_location5 = new MapLocation(targetLocation.x + 0, targetLocation.y - 1);
        MapLocation search_location6 = new MapLocation(targetLocation.x + 1, targetLocation.y + 1);
        MapLocation search_location7 = new MapLocation(targetLocation.x + 1, targetLocation.y + 0);
        MapLocation search_location8 = new MapLocation(targetLocation.x + 1, targetLocation.y - 1);
        if (object.equals(search_location1) || object.equals(search_location2) || object.equals(search_location3) || object.equals(search_location4) || object.equals(search_location5) || object.equals(search_location6) || object.equals(search_location7) || object.equals(search_location8)) {
            return true;
        }
        return false;

    }

    static boolean isOnOuterAdvancedWall(MapLocation targetLocation, MapLocation object) {
        MapLocation search_location1 = new MapLocation(targetLocation.x + -3, targetLocation.y + 2);
        MapLocation search_location2 = new MapLocation(targetLocation.x + -3, targetLocation.y + 1);
        MapLocation search_location3 = new MapLocation(targetLocation.x + -3, targetLocation.y - 0);
        MapLocation search_location4 = new MapLocation(targetLocation.x + -3, targetLocation.y + -1);
        MapLocation search_location5 = new MapLocation(targetLocation.x + -2, targetLocation.y + 2);
        MapLocation search_location6 = new MapLocation(targetLocation.x - 2, targetLocation.y - 1);
        MapLocation search_location7 = new MapLocation(targetLocation.x - 2, targetLocation.y - 2);
        MapLocation search_location8 = new MapLocation(targetLocation.x - 2, targetLocation.y - 3);

        MapLocation search_location9 = new MapLocation(targetLocation.x - 1, targetLocation.y + 3);
        MapLocation search_location10 = new MapLocation(targetLocation.x + -1, targetLocation.y + 2);
        MapLocation search_location11 = new MapLocation(targetLocation.x + -1, targetLocation.y - 3);
        MapLocation search_location12 = new MapLocation(targetLocation.x + 0, targetLocation.y + 3);
        MapLocation search_location13 = new MapLocation(targetLocation.x + 0, targetLocation.y - 3);
        MapLocation search_location14 = new MapLocation(targetLocation.x + 1, targetLocation.y + 3);
        MapLocation search_location15 = new MapLocation(targetLocation.x + 1, targetLocation.y - 2);
        MapLocation search_location16 = new MapLocation(targetLocation.x + 1, targetLocation.y - 3);

        MapLocation search_location17 = new MapLocation(targetLocation.x + 2, targetLocation.y + 3);
        MapLocation search_location18 = new MapLocation(targetLocation.x + 2, targetLocation.y + 2);
        MapLocation search_location19 = new MapLocation(targetLocation.x + 2, targetLocation.y + 1);
        MapLocation search_location20 = new MapLocation(targetLocation.x + 2, targetLocation.y + -2);
        MapLocation search_location21 = new MapLocation(targetLocation.x + 3, targetLocation.y + 1);
        MapLocation search_location22 = new MapLocation(targetLocation.x + 3, targetLocation.y + 0);
        MapLocation search_location23 = new MapLocation(targetLocation.x + 3, targetLocation.y + -1);
        MapLocation search_location24 = new MapLocation(targetLocation.x + 3, targetLocation.y - 2);

        if (object.equals(search_location1) || object.equals(search_location2) || object.equals(search_location3) || object.equals(search_location4) || object.equals(search_location5) || object.equals(search_location6) || object.equals(search_location7) || object.equals(search_location8) || object.equals(search_location9) || object.equals(search_location10) || object.equals(search_location11) || object.equals(search_location12) || object.equals(search_location13) || object.equals(search_location14) || object.equals(search_location15) || object.equals(search_location16) || object.equals(search_location17) || object.equals(search_location18) || object.equals(search_location19) || object.equals(search_location20) || object.equals(search_location21) || object.equals(search_location22) || object.equals(search_location23) || object.equals(search_location24)) {
            return true;
        }
        return false;

    }

    static MapLocation enemyXYSymmetric(MapLocation hqLoc) {
        int dx = rc.getMapWidth() - hqLoc.x - 1;
        int dy = rc.getMapHeight() - hqLoc.y - 1;
        return (new MapLocation(dx, dy));

    }

    static MapLocation enemyYSymmetric(MapLocation hqLoc) {
        int dx = hqLoc.x;
        int dy = rc.getMapHeight() - hqLoc.y - 1;
        return (new MapLocation(dx, dy));

    }

    static MapLocation enemyXSymmetric(MapLocation hqLoc) {
        int dx = rc.getMapWidth() - hqLoc.x - 1;
        int dy = hqLoc.y;
        return (new MapLocation(dx, dy));

    }

    public static void makeMove(Direction move_dir) throws GameActionException {
        if (rc.isReady() && rc.canMove(move_dir)) {
            rc.move(move_dir);

            trail.add(myLoc.add(move_dir));

        }
    }

}