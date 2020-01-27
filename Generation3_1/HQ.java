package Generation3_1;

import battlecode.common.*;

import java.util.Objects;
import java.util.PriorityQueue;


public class HQ extends RobotPlayer {
    static RobotController rc = RobotPlayer.rc;
    static int minerTarget=4+height/20;
    static int constructorTarget=2;
    static int maxMiners=4;
    static int miners_built=0;

    static void runHQ() throws GameActionException {
        System.out.println(Clock.getBytecodeNum());
        myLoc = rc.getLocation();
        myHeight = rc.senseElevation(myLoc);
        System.out.println("HQ ");
        friendlyRobots = rc.senseNearbyRobots(-1, myTeam);
        enemyRobots = rc.senseNearbyRobots(-1, enemyTeam);
        Utility.friendlyRobotScan();
        Utility.enemyRobotScan();

        Communications.checkMessagesQue();
        Communications.clearMessageQue();
        HQButtonedUp=HQButtonUpStatus(myLoc);

        if (enemyRobots.length > 0) {
            for (RobotInfo robotTarget : enemyRobots) {
                if (robotTarget.type == RobotType.DELIVERY_DRONE) {
                    if (myLoc.distanceSquaredTo(robotTarget.location) <= GameConstants.NET_GUN_SHOOT_RADIUS_SQUARED) {
                        if (rc.isReady() && rc.canShootUnit(robotTarget.ID)) {
                            rc.shootUnit(robotTarget.ID);

                        }
                    }
                }
            }
        }

        if (miners_built < 3&&rc.getRoundNum()>1) {
            for (Direction dir : directions)
                if (Utility.tryBuild(RobotType.MINER, dir)) {
                    RobotInfo built_robot=rc.senseRobotAtLocation(myLoc.add(dir));
                    Communications.sendMinerJob(built_robot.ID,0,3);
                    miners_built = miners_built + 1;
                    break;
                }
        }
        if (miners_built ==3&&rc.getTeamSoup()>100) {
            System.out.println(("3rd bot"));
            for (Direction dir : directions)
                if (Utility.tryBuild(RobotType.MINER, dir)) {
                    RobotInfo built_robot=rc.senseRobotAtLocation(myLoc.add(dir));
                    Communications.sendMinerJob(built_robot.ID,1,3);
                    miners_built = miners_built + 1;
                    break;
                }
        }
        if (miners_built >3&&miners_built<minerTarget&&rc.getRoundNum()>200) {
            for (Direction dir : directions)
                if (Utility.tryBuild(RobotType.MINER, dir)) {
                    RobotInfo built_robot=rc.senseRobotAtLocation(myLoc.add(dir));
                    Communications.sendMinerJob(built_robot.ID,0,3);
                    miners_built = miners_built + 1;
                    break;
                }
        }
        Clock.yield();
    }

    static PriorityQueue<Target_Que> prioritizeTargets(RobotInfo[] enemyRobots) {
        MapLocation myLocation = rc.getLocation();
        PriorityQueue<Target_Que> my_prioritized_targets = new PriorityQueue<>();
        int range = 0;
        for (RobotInfo r : enemyRobots) {
            RobotType target_type = r.type;
            if (target_type == RobotType.DELIVERY_DRONE) {
                range = myLocation.distanceSquaredTo(r.location);
                Target_Que p_target = new Target_Que(r, range);
                my_prioritized_targets.add(p_target);
            }
        }
        return my_prioritized_targets;
    }
    static int HQButtonUpStatus(MapLocation myLoc) throws GameActionException {
        int buttonedUp=0;
        for (Direction dir:directions){
            RobotInfo tempBot=rc.senseRobotAtLocation(myLoc.add(dir));
            if (tempBot!=null){
                if (tempBot.type==RobotType.LANDSCAPER){
                    buttonedUp=buttonedUp+1;
                }
            }
        }
        return buttonedUp;
    }


}