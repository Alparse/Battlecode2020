package Generation1_1;

import battlecode.common.*;

import java.lang.annotation.Target;
import java.util.Objects;
import java.util.PriorityQueue;



public class HQ extends RobotPlayer {
    static RobotController rc = RobotPlayer.rc;
    static int minerTarget=6;
    static int constructorTarget=2;
    static int maxMiners=height/4;
    static int miners_built=0;



    static void runHQ() throws GameActionException {
        enemyRobots = rc.senseNearbyRobots(-1, enemyTeam);
        if (enemyRobots.length > 0) {
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
        System.out.println(minerTarget);
        if(rc.getRoundNum()<5) {
            Communications.sendHqLoc(rc.getLocation());
        }
        if (miners_built < minerTarget||rc.getRoundNum()>100&&miners_built<minerTarget+5||rc.getRoundNum()>500&&miners_built<minerTarget+10) {

            for (Direction dir : directions)
                if (Utility.tryBuild(RobotType.MINER, dir)) {
                    miners_built = miners_built + 1;
                }
            ;
            System.out.println("BYTECODES EXECUTED SO FAR 3 " + Clock.getBytecodeNum());
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

}