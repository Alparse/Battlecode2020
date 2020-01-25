package testRobot;

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
        if(rc.getRoundNum()<5) {
            Communications.sendHqLoc(myLoc,3);
        }
        if(messageQue.size()>0){
            for(Message_Que m:messageQue) {
                System.out.println(m.toString());
            }
        }
        if (enemiesNear) {
            PriorityQueue<Target_Que> myTargets = prioritizeTargets(enemyRobots);
            while (!myTargets.isEmpty()) {
                Target_Que myTarget = Objects.requireNonNull(myTargets).poll();
                if (myTarget.getPriority() <= GameConstants.NET_GUN_SHOOT_RADIUS_SQUARED) {
                    if (rc.isReady() && rc.canShootUnit(myTarget.getRobot().ID)) {
                        rc.shootUnit(myTarget.getRobot().ID);
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
        System.out.println("ENEMY HQ LOCATION XY SYMM "+Utility.enemyXYSymmetric(rc.getLocation()));
        System.out.println("ENEMY HQ LOCATION Y SYMM "+Utility.enemyYSymmetric(rc.getLocation()));
        System.out.println("ENEMY HQ LOCATION X SYMM "+Utility.enemyXSymmetric(rc.getLocation()));
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


}