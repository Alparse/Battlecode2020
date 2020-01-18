package Generation1_1;

import battlecode.common.*;


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
}