package Generation1_1;

import battlecode.common.*;

import java.lang.annotation.Target;
import java.util.Objects;
import java.util.PriorityQueue;


public class HQ extends RobotPlayer {
    static RobotController rc = RobotPlayer.rc;


    static void runHQ() throws GameActionException {
        enemyRobots = rc.senseNearbyRobots(-1, enemyTeam);
        if (enemyRobots.length>0){
            PriorityQueue<Target_Que> myTargets=prioritizeTargets(enemyRobots);
            while (!myTargets.isEmpty()){
                Target_Que myTarget= Objects.requireNonNull(myTargets).poll();
                if (myTarget.getPriority()<=GameConstants.NET_GUN_SHOOT_RADIUS_SQUARED){
                    if(rc.isReady() && rc.canShootUnit(myTarget.getRobot().ID)){
                        rc.shootUnit(myTarget.getRobot().ID);
                    }
                }
            }
        }
        if (miners_built<=4) {
            for (Direction dir : directions)
                Utility.tryBuild(RobotType.MINER, dir);
                System.out.println("BYTECODES EXECUTED SO FAR 3 " + Clock.getBytecodeNum());

        }
    }

    static PriorityQueue<Target_Que> prioritizeTargets(RobotInfo[] enemyRobots) {
        MapLocation myLocation = rc.getLocation();
        PriorityQueue<Target_Que> my_prioritized_targets = new PriorityQueue<>();
        int range = 0;
        for (RobotInfo r : enemyRobots) {
            RobotType target_type = r.type;
            if (target_type == RobotType.DELIVERY_DRONE) {
                range = myLocation.distanceSquaredTo(r.location);
                Target_Que p_target=new Target_Que(r,range);
                my_prioritized_targets.add(p_target);
            }
        }
        return my_prioritized_targets;
    }

}