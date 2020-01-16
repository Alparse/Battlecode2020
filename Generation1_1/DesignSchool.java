package Generation1_1;

import battlecode.common.*;

import java.lang.annotation.Target;
import java.util.Objects;
import java.util.PriorityQueue;


public class DesignSchool extends RobotPlayer {
    static RobotController rc = RobotPlayer.rc;

    static void runDesignSchool() throws GameActionException {

        if ((landscapers_built < 8&&landscapers_Nearby()<=8) && rc.getRoundNum() > 200) {

            for (Direction dir : directions)
                if (Utility.tryBuild(RobotType.LANDSCAPER, dir)) {
                    landscapers_built = landscapers_built + 1;
                }
            ;
            System.out.println("BYTECODES EXECUTED SO FAR 3 " + Clock.getBytecodeNum());
        }
    }
    static int landscapers_Nearby() {
        int landscapers = 0;
        RobotInfo[] nearby_Friendlies = rc.senseNearbyRobots(-1, myTeam);
        for (RobotInfo r : nearby_Friendlies) {
            if (r.type == RobotType.LANDSCAPER) {
                landscapers = landscapers + 1;
            }

        }
        return landscapers;
    }

}