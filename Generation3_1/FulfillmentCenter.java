package Generation3_1;

import battlecode.common.*;


public class FulfillmentCenter extends RobotPlayer {
    static RobotController rc = RobotPlayer.rc;
    static int drones_built = 0;

    static void runFulfillmentCenter() throws GameActionException {
        myLoc = rc.getLocation();
        myHeight = rc.senseElevation(myLoc);
        friendlyRobots = rc.senseNearbyRobots(-1, myTeam);
        enemyRobots = rc.senseNearbyRobots(-1, enemyTeam);
        Utility.friendlyRobotScan();
        Utility.enemyRobotScan();
        Communications.checkMessagesQue();
        Communications.clearMessageQue();
        Communications.getConstructionStatus();
        System.out.println(design_centerBuilt + " " + fulfillment_centerBuilt + " " + vaporatorsBuilt);
        int button=HQ.HQButtonUpStatus(hqLoc);
        if ((button>=8)||enemiesNear||rc.getRoundNum()>1000) {
            for (Direction dir : directions)
                if (Utility.tryBuild(RobotType.DELIVERY_DRONE, dir)) {
                    RobotInfo built_robot = rc.senseRobotAtLocation(myLoc.add(dir));
                    drones_built=drones_built+1;
                    //Communications.sendLandScaperJob(built_robot.ID, 10, 3);
                    //landscapers_built = landscapers_built + 1;
                    break;

                }
        }

    }


}