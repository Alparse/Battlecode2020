package Generation1_1;

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
        boolean notTooHigh = !(Math.abs(destinationHeight - myHeight) > 3);
        boolean notOccupied = (rc.senseRobotAtLocation(searchLocation) == null);
        boolean onTheMap = rc.onTheMap(searchLocation);
        return notFlooded && notTooHigh && notOccupied && onTheMap;

    }


    static ArrayList<MapLocation> largeWall(MapLocation targetLocation) throws GameActionException {
        ArrayList<MapLocation> largeWall = new ArrayList<>();

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
                isNotWall(search_location8)||isNotWall(search_location9)||isNotWall(search_location10)||isNotWall(search_location11)||isNotWall(search_location12)||isNotWall(search_location13)||isNotWall(search_location14)||isNotWall(search_location15)||isNotWall(search_location16)) {
            return false;
        }
        return true;
    }
    static MapLocation enemyXYSymmetric(MapLocation hqLoc){
        int dx=rc.getMapWidth()-hqLoc.x-1;
        int dy=rc.getMapHeight()-hqLoc.y-1;
        return( new MapLocation (dx,dy));

    }
    static MapLocation enemyYSymmetric(MapLocation hqLoc){
        int dx=hqLoc.x;
        int dy=rc.getMapHeight()-hqLoc.y-1;
        return( new MapLocation (dx,dy));

    }
    static MapLocation enemyXSymmetric(MapLocation hqLoc){
        int dx=rc.getMapWidth()-hqLoc.x-1;
        int dy=hqLoc.y;
        return( new MapLocation (dx,dy));

    }
}