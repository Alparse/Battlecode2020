package Generation3_1;

import battlecode.common.*;

import java.lang.invoke.SwitchPoint;
import java.util.ArrayList;
import java.util.concurrent.Callable;


public class Drone extends RobotPlayer {
    static RobotController rc = RobotPlayer.rc;


    enum droneState {DRONING, HUNTING, ATTACKINGENEMYHQ}

    static droneState myState = droneState.DRONING;
    static MapLocation swarmCenter = null;
    static MapLocation enemyHQLoc = null;

    static int hqGuess = 1;
    static int wave=0;


    static void runDrone() throws GameActionException {
        friendlyRobots = rc.senseNearbyRobots(-1, myTeam);

        if (hqLoc == null) {
            hqLoc = Communications.getHqLocFromBlockchain();
        }
        if (rc.getRoundNum() < 1000) {
        }
        swarmCenter = hqLoc;

        if(rc.getRoundNum()<800){
            wave=1;
        }
        if(rc.getRoundNum()>=800){
            wave=2;
        }



        while (true) {
            try {

                myLoc = rc.getLocation();
                friendlyRobots = rc.senseNearbyRobots(-1, myTeam);
                enemyRobots = rc.senseNearbyRobots(-1, enemyTeam);
                Utility.friendlyRobotScan();
                Utility.enemyRobotScan();
                Communications.checkMessagesQue();
                Communications.clearMessageQue();


                if (hqGuess == 1) {
                    enemyHQLoc = Utility.enemyYSymmetric(hqLoc);
                }
                if (hqGuess == 2) {
                    enemyHQLoc = Utility.enemyXYSymmetric(hqLoc);
                }
                if (hqGuess == 3) {
                    enemyHQLoc = Utility.enemyXSymmetric(hqLoc);
                }
                if (hqGuess == 4) {
                    enemyHQLoc = enemyHQLoc;
                }
                if (rc.getRoundNum() <= 800) {
                    swarmCenter = hqLoc;
                }
                if (rc.getRoundNum() > 800) {
                    if (wave==1) {
                        swarmCenter = enemyHQLoc;
                    }
                }
                if (rc.getRoundNum() > 1800) {
                    if (wave==1 ||wave==2) {
                        swarmCenter = enemyHQLoc;
                    }
                }



                if (myLoc.distanceSquaredTo(enemyHQLoc) < RobotType.DELIVERY_DRONE.sensorRadiusSquared) {
                    if (rc.canSenseLocation(enemyHQLoc)) {
                        if (rc.senseRobotAtLocation(enemyHQLoc) != null) {
                            if (rc.senseRobotAtLocation(enemyHQLoc).type != RobotType.HQ) {
                                hqGuess = hqGuess + 1;
                            }
                            if (rc.senseRobotAtLocation(enemyHQLoc).type == RobotType.HQ) {
                                hqGuess = 4;

                            }
                        }
                    }
                }


                switch (myState) {
                    case DRONING:
                        if(enemyRobots.length>0&&!rc.isCurrentlyHoldingUnit()){
                            Direction move_dir=myLoc.directionTo(enemyRobots[0].location);
                            makeMove(move_dir);
                        }
                        pickupEnemy();

                        if (rc.isCurrentlyHoldingUnit()) {
                            Direction move_dir = myLoc.directionTo(swarmCenter).opposite();
                            if (rc.senseFlooding(myLoc)) {
                                if (rc.isReady()) {
                                    for (Direction dir : directions) {
                                        if (rc.canDropUnit(dir)) {
                                            rc.dropUnit(dir);
                                        }
                                    }
                                }
                            }
                            makeMove(move_dir);
                        }

                        if (swarmCenter == hqLoc||!enemiesNear) {
                            if (!rc.isCurrentlyHoldingUnit() && myLoc.distanceSquaredTo(hqLoc)>20) {
                                Direction move_dir = myLoc.directionTo(swarmCenter);
                                makeMove(move_dir);
                            }
                            if (!rc.isCurrentlyHoldingUnit() && myLoc.distanceSquaredTo(hqLoc)<=20&&myLoc.distanceSquaredTo(hqLoc)>13) {
                                Direction move_dir = randomDirection();
                                makeMove(move_dir);
                            }
                            if (!rc.isCurrentlyHoldingUnit() && myLoc.distanceSquaredTo(hqLoc)<=13) {
                                if (swarmCenter == hqLoc) {
                                    Direction move_dir = myLoc.directionTo(hqLoc).opposite();
                                    makeMove(move_dir);
                                }
                            }
                        }

                        if (swarmCenter != hqLoc) {
                            Direction move_dir = myLoc.directionTo(swarmCenter);
                            makeMove(move_dir);
                        }


                        break;

                    case HUNTING:

                        break;
                    case ATTACKINGENEMYHQ:

                        break;

                }

                //Clock.yield();
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
        RobotInfo[] close_enemies = rc.senseNearbyRobots(8, enemyTeam);
        if (close_enemies.length > 0) {
            for (RobotInfo enemyRobot : close_enemies) {
                if (rc.isReady()) {
                    if (rc.canPickUpUnit(enemyRobot.ID)) {
                        rc.pickUpUnit(enemyRobot.ID);
                    }
                }
            }

        }
    }

}

