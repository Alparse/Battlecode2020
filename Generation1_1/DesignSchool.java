package Generation1_1;

import battlecode.common.*;

import java.lang.annotation.Target;
import java.util.Objects;
import java.util.PriorityQueue;


public class DesignSchool extends RobotPlayer {
    static RobotController rc = RobotPlayer.rc;
    static int landscapers_built=0;

    static void runDesignSchool() throws GameActionException {
        myLoc = rc.getLocation();
        myHeight = rc.senseElevation(myLoc);
        friendlyRobots = rc.senseNearbyRobots(-1, myTeam);
        enemyRobots = rc.senseNearbyRobots(-1, enemyTeam);
        Utility.friendlyRobotScan();
        Utility.enemyRobotScan();
        Communications.checkMessagesQue();
        Communications.clearMessageQue();

        if ((landscapers_built < 2&&rc.getTeamSoup()>200)||(enemyLandscaperNear&&landscapers_built<8)&&rc.getTeamSoup()>=200) {
            for (Direction dir : directions)
                if (Utility.tryBuild(RobotType.LANDSCAPER, dir)) {
                    RobotInfo built_robot=rc.senseRobotAtLocation(myLoc.add(dir));
                    Communications.sendLandScaperJob(built_robot.ID,10,3);
                    landscapers_built = landscapers_built+ 1;
                    break;
                }
        }
        if (landscapers_built == 2&&rc.getTeamSoup()>=200) {
            for (Direction dir : directions)
                if (Utility.tryBuild(RobotType.LANDSCAPER, dir)) {
                    RobotInfo built_robot=rc.senseRobotAtLocation(myLoc.add(dir));
                    Communications.sendLandScaperJob(built_robot.ID,11,3);
                    landscapers_built = landscapers_built+ 1;
                    break;
                }
        }

        if ((landscapers_built >=3&&landscapers_built<13&&rc.getTeamSoup()>=200) && rc.getRoundNum() > 200) {
            for (Direction dir : directions)
                if (Utility.tryBuild(RobotType.LANDSCAPER, dir)) {
                    RobotInfo built_robot=rc.senseRobotAtLocation(myLoc.add(dir));
                    Communications.sendLandScaperJob(built_robot.ID,10,3);
                    landscapers_built = landscapers_built+ 1;
                    break;
                }
        }
    }

}