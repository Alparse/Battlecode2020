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
}