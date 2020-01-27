package Generation3_1;

import battlecode.common.*;


public class DesignSchool extends RobotPlayer {
    static RobotController rc = RobotPlayer.rc;
    static int landscapers_built = 0;



    static void runDesignSchool() throws GameActionException {
        myLoc = rc.getLocation();
        myHeight = rc.senseElevation(myLoc);
        friendlyRobots = rc.senseNearbyRobots(-1, myTeam);
        enemyRobots = rc.senseNearbyRobots(-1, enemyTeam);
        Utility.friendlyRobotScan();
        Utility.enemyRobotScan();
        Communications.checkMessagesQue();
        Communications.clearMessageQue();
        Communications.getConstructionStatus();
        scanNearbyBuildings();


        if (rc.getRoundNum() > 50) {
            if ((landscapers_built < 2 && rc.getTeamSoup() > 200)) {
                for (Direction dir : directions)
                    if (Utility.tryBuild(RobotType.LANDSCAPER, dir)) {
                        RobotInfo built_robot = rc.senseRobotAtLocation(myLoc.add(dir));
                        Communications.sendLandScaperJob(built_robot.ID, 10, 3);
                        landscapers_built = landscapers_built + 1;
                        break;
                    }
            }

            if (( vaporatorsBuilt == 2) || rc.getRoundNum() > 650||enemyRobots.length>0){

                if (landscapers_built < 18 && rc.getTeamSoup() >= 200) {
                    for (Direction dir : directions)
                        if (Utility.tryBuild(RobotType.LANDSCAPER, dir)) {
                            RobotInfo built_robot = rc.senseRobotAtLocation(myLoc.add(dir));
                            Communications.sendLandScaperJob(built_robot.ID, 11, 3);
                            landscapers_built = landscapers_built + 1;
                            break;
                        }
                }
                }
        }
    }
    static void scanNearbyBuildings(){
        int vaporators=0;
        for (RobotInfo nearbyRobot:friendlyRobots){
            if (nearbyRobot.type==RobotType.FULFILLMENT_CENTER) {
                fulfillment_centerBuilt = 1;
            }
            if (nearbyRobot.type==RobotType.DESIGN_SCHOOL) {
                design_centerBuilt = 1;
            }
            if (nearbyRobot.type==RobotType.VAPORATOR) {
                vaporators = vaporators + 1;
            }
        }
        vaporatorsBuilt=vaporators;
    }

}