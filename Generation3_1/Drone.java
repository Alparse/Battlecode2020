package Generation3_1;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.concurrent.Callable;


public class Drone extends RobotPlayer {
    static RobotController rc = RobotPlayer.rc;


    enum droneState {DRONING, HUNTING, ATTACKINGENEMYHQ}

    static droneState myState = droneState.DRONING;
    static MapLocation swarmCenter=null;

    static void runDrone() throws GameActionException {
        friendlyRobots = rc.senseNearbyRobots(-1, myTeam);

        if (hqLoc == null) {
            hqLoc = Communications.getHqLocFromBlockchain();
        }
        swarmCenter=hqLoc;

        while (true) {
            try {
                System.out.println("BYTECODE START " + Clock.getBytecodeNum());
                myLoc = rc.getLocation();
                friendlyRobots = rc.senseNearbyRobots(-1, myTeam);
                enemyRobots = rc.senseNearbyRobots(-1, enemyTeam);
                Utility.friendlyRobotScan();
                Utility.enemyRobotScan();
                Communications.checkMessagesQue();
                Communications.clearMessageQue();

               if(rc.getRoundNum()>1000&&rc.getRoundNum()<1200){
                   swarmCenter=Utility.enemyYSymmetric(hqLoc);
               }

                if(rc.getRoundNum()>=1200&&rc.getRoundNum()<=1400){
                    swarmCenter=Utility.enemyXSymmetric(hqLoc);
                }


                if(rc.getRoundNum()>1400){
                    swarmCenter=Utility.enemyXYSymmetric(hqLoc);
                }


                switch (myState) {
                    case DRONING:
                        System.out.println("DRONING");
                        pickupEnemy();

                        if (rc.isCurrentlyHoldingUnit()){
                            Direction move_dir= myLoc.directionTo(swarmCenter).opposite();
                            if (rc.senseFlooding(myLoc)||myLoc.distanceSquaredTo(swarmCenter)>100){
                                if (rc.isReady()){
                                    for(Direction dir:directions) {
                                        if (rc.canDropUnit(dir)) {
                                            rc.dropUnit(dir);
                                        }
                                    }
                                }
                            }
                            makeMove(move_dir);
                        }
                        if(!rc.isCurrentlyHoldingUnit()&&!myLoc.isAdjacentTo(hqLoc)) {
                            Direction move_dir = myLoc.directionTo(swarmCenter);
                            makeMove(move_dir);
                        }
                        if(!rc.isCurrentlyHoldingUnit()&&myLoc.isAdjacentTo(hqLoc)) {
                            Direction move_dir = randomDirection();
                            makeMove(move_dir);
                        }

                        break;

                    case HUNTING:
                        System.out.println("HUNTING");
                        break;
                    case ATTACKINGENEMYHQ:
                        System.out.println("ATTACKING ENEMYHQ");
                        break;

                }

                Clock.yield();
                System.out.println("BYTECODE END " + Clock.getBytecodeNum());
            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");

                e.printStackTrace();

            }
        }

    }


    public static void makeMove(Direction move_dir) throws GameActionException {
        if (rc.isReady()) {
            if (rc.canMove(move_dir)) {
                rc.move(move_dir);
                return;
            }
        }
        if (rc.isReady() && !rc.canMove(move_dir)) {
            for (Direction dir : directions) {
                if (rc.canMove(move_dir.rotateLeft())) {
                    move_dir = move_dir.rotateLeft();
                    break;
                }
                if (rc.canMove(move_dir.rotateRight())) {
                    move_dir = move_dir.rotateRight();
                    break;
                }
                if (rc.canMove(move_dir.rotateLeft().rotateLeft())) {
                    move_dir = move_dir.rotateLeft().rotateLeft();
                    break;
                }
                if (rc.canMove(move_dir.rotateRight().rotateRight())) {
                    move_dir = move_dir.rotateRight().rotateRight();
                    break;
                }
                if (rc.canMove(move_dir.rotateLeft().rotateLeft().rotateLeft())) {
                    move_dir = move_dir.rotateLeft().rotateLeft().rotateLeft();
                    break;
                }
                if (rc.canMove(move_dir.rotateRight().rotateRight().rotateRight())) {
                    move_dir = move_dir.rotateRight().rotateRight().rotateRight();
                    break;
                }
                if (rc.canMove(move_dir.rotateLeft().rotateLeft().rotateLeft().rotateLeft())) {
                    move_dir = move_dir.rotateLeft().rotateLeft().rotateLeft().rotateLeft();
                    break;
                }
                if (rc.canMove(move_dir.rotateRight().rotateRight().rotateRight().rotateRight())) {
                    move_dir = move_dir.rotateRight().rotateRight().rotateRight().rotateRight();
                    break;
                }
                if (rc.canMove(move_dir.opposite())) {
                    move_dir = move_dir.opposite();
                    break;
                }
            }
        }
        if (rc.canMove(move_dir)) {
            rc.move(move_dir);
        }
    }


    static Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }
    static void pickupEnemy() throws GameActionException {
        RobotInfo[] close_enemies=rc.senseNearbyRobots(8,enemyTeam);
        if(close_enemies.length>0){
            for (RobotInfo enemyRobot:close_enemies){
                if (rc.isReady()){
                    if(rc.canPickUpUnit(enemyRobot.ID)){
                        rc.pickUpUnit(enemyRobot.ID);
                    }
                }
            }

        }
    }

}

