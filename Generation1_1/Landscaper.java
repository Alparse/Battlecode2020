package Generation1_1;

import battlecode.common.*;
import gnu.trove.impl.sync.TSynchronizedShortByteMap;

import javax.swing.*;
import java.awt.*;
import java.sql.Driver;


public class Landscaper extends RobotPlayer {
    static RobotController rc = RobotPlayer.rc;
    static MapLocation soupLoc = null;
    static boolean levee_builder = true;
    static boolean grave_digger = false;
    static boolean at_spot = false;
    static int landScaperJob = 99;
    static boolean checkedXYSymetricEnemyHQ = false;
    static boolean checkedXSymetricEnemyHQ = false;
    static boolean checkedYSymetricEnemyHQ = false;
    static MapLocation enemyHQGuess = null;
    static MapLocation enemyHQ = null;
    static boolean communicatedEnemyHQ = false;


    static void runLandscaper() throws GameActionException {
        if (rc.getRoundNum() > 0) {
            landScaperJob = Communications.getLandScaperFromBlockchain();
            System.out.println("MY JOB IS " + landScaperJob);
        }
        if (hqLoc == null) {
            hqLoc = Communications.getHqLocFromBlockchain();
        }

        System.out.println("BYTECODE START " + Clock.getBytecodeNum());
        myLoc = rc.getLocation();
        myHeight = rc.senseElevation(myLoc);
        mother_Nearby();

        while (true) {
            try {
                if (landScaperJob == 99) {
                    landScaperJob = Communications.getLandScaperFromBlockchain();
                }
                System.out.println("CLOCK START 1 " + Clock.getBytecodesLeft());
                Communications.checkMessagesQue();
                Communications.clearMessageQue();
                friendlyRobots = rc.senseNearbyRobots(-1, myTeam);
                enemyRobots = rc.senseNearbyRobots(-1, enemyTeam);
                Utility.friendlyRobotScan();
                Utility.enemyRobotScan();
                myLoc = rc.getLocation();
                myHeight = rc.senseElevation(myLoc);
                if (trail.size() > 4) {
                    trail.remove(trail.remove(0));
                }
                if (landScaperJob == 10 || landScaperJob == 99) {
                    System.out.println("CLOCK START 2 " + Clock.getBytecodesLeft());
                    System.out.println("STARTING LANDSCAPE 10 ");
                    if (hqLoc == null) {
                        hqLoc = Communications.getHqLocFromBlockchain();
                    }

                    System.out.println("HQ LOC " + hqLoc);
                    if (largeWall.size() == 0) {
                        largeWall = Utility.largeWall(hqLoc);
                        System.out.println(largeWall);
                    }
                    Direction dig_dirt_dir = null;
                    if (myLoc.isAdjacentTo(hqLoc)) {
                        System.out.println("AT SPOT");
                        System.out.println("CLOCK START 3 " + Clock.getBytecodesLeft());
                        if (hqLoc.directionTo(myLoc) != Direction.NORTH && hqLoc.directionTo(myLoc) != Direction.SOUTH && hqLoc.directionTo(myLoc) != Direction.EAST && hqLoc.directionTo(myLoc) != Direction.WEST) {
                            if (rc.canMove(myLoc.directionTo(hqLoc.add(Direction.EAST)))) {
                                rc.move(myLoc.directionTo(hqLoc.add(Direction.EAST)));
                            }
                            if (rc.canMove(myLoc.directionTo(hqLoc.add(Direction.WEST)))) {
                                rc.move(myLoc.directionTo(hqLoc.add(Direction.WEST)));
                            }
                            if (rc.canMove(myLoc.directionTo(hqLoc.add(Direction.SOUTH)))) {
                                rc.move(myLoc.directionTo(hqLoc.add(Direction.SOUTH)));
                            }
                            if (rc.canMove(myLoc.directionTo(hqLoc.add(Direction.NORTH)))) {
                                rc.move(myLoc.directionTo(hqLoc.add(Direction.NORTH)));
                            }
                        }
                        if (rc.getDirtCarrying() == 0) {
                            if ((enemyLandscaperNear || rc.getRoundNum() > 200)) {
                                Direction dirFromHQ = hqLoc.directionTo(myLoc);
                                MapLocation targetDigLocation = myLoc.add(hqLoc.directionTo(myLoc));
                                if (dirFromHQ == Direction.NORTH || dirFromHQ == Direction.NORTHWEST || dirFromHQ == Direction.NORTHEAST) {
                                    targetDigLocation = new MapLocation(hqLoc.x, hqLoc.y + 2);
                                }
                                if (dirFromHQ == Direction.SOUTH || dirFromHQ == Direction.SOUTHWEST || dirFromHQ == Direction.SOUTHEAST) {
                                    targetDigLocation = new MapLocation(hqLoc.x, hqLoc.y - 2);
                                }
                                if (dirFromHQ == Direction.EAST) {
                                    targetDigLocation = new MapLocation(hqLoc.x + 2, hqLoc.y);
                                }
                                if (dirFromHQ == Direction.WEST) {
                                    targetDigLocation = new MapLocation(hqLoc.x - 2, hqLoc.y);
                                }

                                dig_dirt_dir = myLoc.directionTo(targetDigLocation);
                                if (!rc.onTheMap(myLoc.add(dig_dirt_dir))||!rc.canDigDirt(dig_dirt_dir)) {
                                    for (Direction d : directions) {
                                        MapLocation testLocation = myLoc.add(d);
                                        if (rc.onTheMap(testLocation)) {
                                            if (!hqLoc.isAdjacentTo(testLocation)&&rc.canDigDirt(d)) {
                                                dig_dirt_dir = d;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (buildingBeingBuried(hqLoc)) {
                                    dig_dirt_dir = myLoc.directionTo(hqLoc);
                                }
                                if (rc.canDigDirt(dig_dirt_dir)) {
                                    if (rc.isReady()) {
                                        rc.digDirt(dig_dirt_dir);
                                        System.out.println("DUG DIRT");
                                    }
                                } else {
                                    for (Direction d : directions) {
                                        if (rc.canDigDirt(dig_dirt_dir)) {
                                            if (rc.isReady()) {
                                                rc.digDirt(dig_dirt_dir);
                                                System.out.println("DUG DIRT");
                                                break;
                                            }
                                        }
                                    }

                                }
                            }
                        }
                        if (rc.getDirtCarrying() > 0) {
                            Direction deposit_dir = dirtScan();
                            System.out.println(" DIRT SCAN DISTANCE " + deposit_dir);
                            if (deposit_dir != null) {
                                if (rc.canDepositDirt(deposit_dir)) {
                                    if (rc.isReady()) {
                                        rc.depositDirt(deposit_dir);
                                        System.out.println("DEPOSITED DIRT " + deposit_dir);
                                        break;
                                    }
                                }
                            }
                            if (rc.canDepositDirt(Direction.CENTER)) {
                                if (rc.isReady()) {
                                    rc.depositDirt(Direction.CENTER);
                                    System.out.println("DEPOSITED DIRT CENTER");
                                }

                            }
                        }
                    }
                    if (levee_builder && !myLoc.isAdjacentTo(hqLoc)) {
                        RobotInfo enemyBuild = enemyBuildingNear();
                        if (enemyBuild == null) {
                            System.out.println("LEVEE BUILDER" + hqLoc);
                            System.out.println("BUG MOVE DIR start");
                            Direction move_dir = Bug1.BugGetNext(hqLoc);
                            System.out.println("BUG MOVE DIR " + move_dir);
                            makeMove(move_dir);
                        }
                        if (enemyBuild != null && !myLoc.isAdjacentTo(enemyBuild.location)) {
                            System.out.println("ATTACK ENEMY BUILDING" + enemyBuild.location);
                            System.out.println("BUG MOVE DIR start");
                            Direction move_dir = Bug1.BugGetNext(enemyBuild.location);
                            System.out.println("BUG MOVE DIR " + move_dir);
                            makeMove(move_dir);
                        }
                        if (enemyBuild != null && myLoc.isAdjacentTo(enemyBuild.location)) {
                            if (rc.canDigDirt(Direction.CENTER)) {
                                if (rc.isReady()) {
                                    rc.digDirt(Direction.CENTER);
                                }
                            }
                        }
                        if (rc.getDirtCarrying() == RobotType.LANDSCAPER.dirtLimit) {
                            if (enemyBuild != null) {
                                Direction deposit_dir = myLoc.directionTo(enemyBuild.location);
                                if (deposit_dir != null) {
                                    if (rc.canDepositDirt(deposit_dir)) {
                                        if (rc.isReady()) {
                                            rc.depositDirt(deposit_dir);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (landScaperJob == 11) {
                    System.out.println("STARTING HUNT " + enemyHQ);
                    RobotInfo enemyBuild = enemyBuildingNear();
                    if (enemyHQ == null) {
                        if (!checkedXYSymetricEnemyHQ) {
                            enemyHQGuess = Utility.enemyXYSymmetric(hqLoc);
                            Direction move_dir = Bug1.BugGetNext(enemyHQGuess);
                            makeMove(move_dir);
                            if (rc.canSenseLocation(enemyHQGuess)) {
                                checkedXYSymetricEnemyHQ = true;
                                System.out.println("1");
                            }
                        }
                        if (checkedXYSymetricEnemyHQ && !checkedXSymetricEnemyHQ) {
                            enemyHQGuess = Utility.enemyXSymmetric(hqLoc);
                            Direction move_dir = Bug1.BugGetNext(enemyHQGuess);
                            makeMove(move_dir);
                            if (rc.canSenseLocation(enemyHQGuess)) {
                                checkedXSymetricEnemyHQ = true;
                                System.out.println("2");
                            }
                        }
                        if (checkedXYSymetricEnemyHQ && checkedXSymetricEnemyHQ && !checkedYSymetricEnemyHQ) {
                            enemyHQGuess = Utility.enemyYSymmetric(hqLoc);
                            Direction move_dir = Bug1.BugGetNext(enemyHQGuess);
                            makeMove(move_dir);
                            if (rc.canSenseLocation(enemyHQGuess)) {
                                checkedYSymetricEnemyHQ = true;
                                System.out.println("3");
                            }
                        }
                    }

                    enemyBuild = enemyBuildingNear();
                    if (enemyHQ != null && !communicatedEnemyHQ && rc.getTeamSoup() > 20) {
                        Communications.sendEnemyHqLoc(enemyHQ, 6);
                        communicatedEnemyHQ = true;
                    }

                    if (enemyHQ != null && enemyBuild == null) {
                        System.out.println("Found Enemy HQ " + enemyHQ);
                        Direction move_dir = Bug1.BugGetNext(enemyHQ);
                        System.out.println("BUG MOVE DIR TO ENEMY HQ" + move_dir);
                        makeMove(move_dir);

                    }


                    if (enemyBuild != null && !myLoc.isAdjacentTo(enemyBuild.location)) {
                        System.out.println("ATTACK ENEMY BUILDING" + enemyBuild.location);
                        System.out.println("BUG MOVE DIR start");
                        Direction move_dir = Bug1.BugGetNext(enemyBuild.location);
                        System.out.println("BUG MOVE DIR " + move_dir);
                        makeMove(move_dir);
                    }
                    if (enemyBuild != null && myLoc.isAdjacentTo(enemyBuild.location)) {
                        if (enemyBuild.type == RobotType.HQ) {
                            if (rc.canDigDirt(Direction.CENTER)) {
                                if (rc.isReady()) {
                                    rc.digDirt(Direction.CENTER);
                                }
                            }
                        }
                        if (enemyBuild.type != RobotType.HQ) {
                            for (Direction dir : directions)
                                if (rc.canDigDirt(dir)) {
                                    if (rc.isReady()) {
                                        rc.digDirt(dir);
                                        return;
                                    }
                                }
                            if (rc.canDigDirt(Direction.CENTER)) {
                                if (rc.isReady()) {
                                    rc.digDirt(Direction.CENTER);
                                }
                            }
                        }
                    }
                    if (rc.getDirtCarrying() == RobotType.LANDSCAPER.dirtLimit) {
                        if (enemyBuild != null) {
                            Direction deposit_dir = myLoc.directionTo(enemyBuild.location);
                            if (deposit_dir != null) {
                                if (rc.canDepositDirt(deposit_dir)) {
                                    if (rc.isReady()) {
                                        rc.depositDirt(deposit_dir);
                                    }
                                }
                            }
                        }
                    }


                }

                System.out.println("BYTECODE END " + Clock.getBytecodeNum());
                System.out.println("DIRT CARRYING " + rc.getDirtCarrying());
                Clock.yield();
            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");

                e.printStackTrace();
            }

        }
    }

    public static void makeMove(Direction move_dir) throws GameActionException {
        if (rc.isReady() && rc.canMove(move_dir)) {
            rc.move(move_dir);
            System.out.println("MOVE DIR IS " + move_dir);
            trail.add(myLoc.add(move_dir));

        }
    }


    static Direction dirtScan() throws GameActionException {

        myLoc = rc.getLocation();
        for (Direction dir : directions)
            if (!myLoc.add(dir).equals(hqLoc) && myLoc.add(dir).isAdjacentTo(hqLoc)) {
                int height_dif = rc.senseElevation(myLoc) - rc.senseElevation(myLoc.add(dir));
                System.out.println("HQ LOC " + hqLoc);
                if (height_dif > RobotType.LANDSCAPER.dirtLimit) {
                    System.out.println("DUMP LOCATION " + dir);
                    return dir;
                }
            }

        return null;
    }

    static boolean mother_Nearby() {
        RobotInfo[] nearby_Friendlies = rc.senseNearbyRobots(-1, myTeam);
        for (RobotInfo r : nearby_Friendlies) {
            if (myType == RobotType.MINER) {
                if (r.type == RobotType.HQ) {
                    hqLoc = r.location;
                    return true;
                }
                if (r.type == RobotType.REFINERY) {
                    hqLoc = r.location;
                    return true;
                }
            }
        }
        return false;
    }


    static boolean buildingBeingBuried(MapLocation building_loc) throws GameActionException {
        RobotInfo building = rc.senseRobotAtLocation(building_loc);
        if (building.dirtCarrying > 0) {
            return true;
        }
        return false;
    }

    static RobotInfo enemyBuildingNear() {
        if (enemyRobots.length > 0) {
            for (RobotInfo r : enemyRobots) {
                if (r.type == RobotType.DESIGN_SCHOOL) {
                    return r;
                }
                if (r.type == RobotType.HQ) {
                    enemyHQ = r.location;
                    return r;
                }
                if (r.type == RobotType.FULFILLMENT_CENTER) {
                    return r;
                }
                if (r.type == RobotType.VAPORATOR) {
                    return r;
                }
                if (r.type == RobotType.NET_GUN) {
                    return r;
                }
            }
        }
        return null;
    }

    static void levelGround(Direction direction) throws GameActionException {
        int my_height = rc.senseElevation(myLoc);
        int target_height = rc.senseElevation(myLoc.add(direction));
        if (my_height - 3 > target_height) {
            if (rc.isReady()) {
                rc.digDirt(Direction.CENTER);
                rc.depositDirt(direction);
            }
        }
        if (my_height < target_height - 3) {
            if (rc.isReady()) {
                rc.digDirt(direction);
                rc.depositDirt(Direction.CENTER);

            }
        }

    }
}
