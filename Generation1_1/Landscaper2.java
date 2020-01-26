package Generation1_1;

import battlecode.common.*;

import java.util.ArrayList;

public class Landscaper2 extends RobotPlayer {

    enum landscaperState {START, TRAVELINGOUTERWALL, TRAVELINGINNERWALL, TERRAFORMING, MOVINGONOUTERWALL, BUILDINGOUTERWALL, MANNINGOUTERWALL, BUILDINGINNERWALL, MOVINGINNERWALL, MANNINGINNERWALL, RESCUINGBUILDING, ATTACKINGBUILDING}

    static landscaperState myState = landscaperState.TRAVELINGOUTERWALL;

    static RobotController rc = RobotPlayer.rc;
    static int landScaperJob = 99;
    static ArrayList<MapLocation> digLocations = null;
    static ArrayList<MapLocation> outerWallLocations = null;
    static ArrayList<MapLocation> innerWallLocations = null;
    static MapLocation lastLoc = null;
    static boolean movedOnWall = false;
    static boolean movingClockwise = false;
    static boolean movingCounterClockwise = true;

    static void runLandscaper() throws GameActionException {
        if (rc.getRoundNum() > 0) {
            landScaperJob = Communications.getLandScaperFromBlockchain();
            System.out.println("MY JOB IS " + landScaperJob);
        }
        if (hqLoc == null) {
            hqLoc = Communications.getHqLocFromBlockchain();
        }


        System.out.println("BYTECODE START " + Clock.getBytecodeNum());

        digLocations = Utility.digLocations(hqLoc);
        outerWallLocations = Utility.outerWallArray(hqLoc);
        innerWallLocations = Utility.innerWallArray(hqLoc);

        if (rc.getRoundNum() > 300) {
            if (myLoc.isAdjacentTo(hqLoc)) {
                myState = landscaperState.MOVINGINNERWALL;
            }
        }
        while (true) {
            try {
                myLoc = rc.getLocation();
                myHeight = rc.senseElevation(myLoc);
                friendlyRobots = rc.senseNearbyRobots(-1, myTeam);
                enemyRobots = rc.senseNearbyRobots(-1, enemyTeam);
                Utility.friendlyRobotScan();
                Utility.enemyRobotScan();


                switch (myState) {

                    case TRAVELINGOUTERWALL:
                        System.out.println(myState);
                        if (outerWallLocations.contains(myLoc)) {
                            myState = landscaperState.MOVINGONOUTERWALL;
                            break;
                        }

                        if (!outerWallLocations.contains(myLoc)) {
                            MapLocation target_location = closestWallLocation(outerWallLocations);
                            Direction move_dir = Bug1.BugGetNext(target_location);
                            Utility.makeMove(move_dir);
                        }
                        break;

                    case MOVINGONOUTERWALL:
                        int nextIndex = 0;
                        System.out.println(myState);
                        if (!outerWallLocations.contains(myLoc)) {
                            myState = landscaperState.TRAVELINGOUTERWALL;
                            break;
                        }

                        if (outerWallLocations.contains(myLoc)) {
                            nextIndex = getNextIndex(myLoc);
                            Direction move_dir = myLoc.directionTo(outerWallLocations.get(nextIndex));
                            if (rc.canMove(move_dir) && !rc.senseFlooding(myLoc.add(move_dir))) {
                                rc.move(move_dir);
                                myState = landscaperState.BUILDINGOUTERWALL;
                                System.out.println("CAN MOVE TO NEXT WALL STEP 2");
                                break;
                            }
                        }
                        break;


                    case BUILDINGOUTERWALL:
                        myLoc = rc.getLocation();
                        myHeight = rc.senseElevation(myLoc);
                        System.out.println(myState);
                        //check next step;
                        nextIndex = 0;
                        if (rc.getDirtCarrying() == 0) {
                            getDirt(myLoc);
                        }
                        nextIndex = getNextIndex(myLoc);


                        if (rc.senseElevation(outerWallLocations.get(nextIndex)) - myHeight >= 3) {
                            //add to my location till ok
                            if (rc.canDepositDirt(Direction.CENTER)) {
                                if (rc.isReady()) {
                                    rc.depositDirt(Direction.CENTER);
                                    myState = landscaperState.MOVINGONOUTERWALL;
                                    break;
                                }
                            }
                        }
                        if (rc.senseElevation(outerWallLocations.get(nextIndex)) - myHeight < 3) {
                            //add to next location till ok
                            if (rc.canDepositDirt(myLoc.directionTo(outerWallLocations.get(nextIndex)))) {
                                if (rc.isReady()) {
                                    rc.depositDirt(myLoc.directionTo(outerWallLocations.get(nextIndex)));
                                    myState = landscaperState.MOVINGONOUTERWALL;
                                    break;
                                }
                            }
                        }
                        break;

                    case TRAVELINGINNERWALL:
                        System.out.println(myState);
                        if (innerWallLocations.contains(myLoc)) {
                            myState = landscaperState.MOVINGINNERWALL;
                            break;
                        }

                        if (!innerWallLocations.contains(myLoc)) {
                            MapLocation target_location = closestWallLocation(innerWallLocations);
                            Direction move_dir = Bug1.BugGetNext(target_location);
                            Utility.makeMove(move_dir);
                        }
                        break;

                    case MOVINGINNERWALL:
                        System.out.println(myState);
                        if (!innerWallLocations.contains(myLoc)) {
                            myState = landscaperState.TRAVELINGINNERWALL;
                        }
                        if (innerWallLocations.contains(myLoc)) {
                            nextIndex = getNextIndex(myLoc);
                            if (Math.abs(rc.senseElevation(innerWallLocations.get(nextIndex)) - myHeight) > 3) {
                                myState = landscaperState.BUILDINGINNERWALL;
                                break;
                            }
                            Direction move_dir = myLoc.directionTo(innerWallLocations.get(nextIndex));
                            if (rc.canMove(move_dir) && !rc.senseFlooding(myLoc.add(move_dir))) {
                                rc.move(move_dir);
                                myState = landscaperState.BUILDINGINNERWALL;
                            }

                        }
                        break;

                    case BUILDINGINNERWALL:
                        System.out.println(myState);
                        //check next step;
                        if (rc.getDirtCarrying() == 0) {
                            getDirt(myLoc);
                        }
                        nextIndex = getNextIndex(myLoc);

                        if (rc.senseElevation(innerWallLocations.get(nextIndex)) - myHeight >= 3) {
                            //add to my location till ok
                            if (rc.canDepositDirt(Direction.CENTER)) {
                                if (rc.isReady()) {
                                    rc.depositDirt(Direction.CENTER);
                                    myState = landscaperState.MOVINGINNERWALL;
                                }
                            }
                        }
                        if (rc.senseElevation(innerWallLocations.get(nextIndex)) - myHeight < 3) {
                            //add to next location till ok
                            if (rc.canDepositDirt(myLoc.directionTo(innerWallLocations.get(nextIndex)))) {
                                if (rc.isReady()) {
                                    rc.depositDirt(myLoc.directionTo(innerWallLocations.get(nextIndex)));
                                    myState = landscaperState.MOVINGINNERWALL;
                                }
                            }
                        }
                        break;


                }


            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");

                e.printStackTrace();
            }
        }

    }

    static MapLocation closestWallLocation(ArrayList<MapLocation> wall) {
        int min_distance = 9999;
        MapLocation min_location = myLoc;
        for (MapLocation testLoc : wall) {
            int cur_distance = myLoc.distanceSquaredTo(testLoc);
            if (cur_distance < min_distance) {
                min_location = testLoc;
            }
        }
        return min_location;

    }

    static void getDirt(MapLocation myLoc) throws GameActionException {
        for (MapLocation digLoc : digLocations) {
            if (myLoc.isAdjacentTo(digLoc) && rc.isReady()) {
                if (rc.canDigDirt(myLoc.directionTo(digLoc))) {
                    rc.digDirt(myLoc.directionTo(digLoc));
                    return;
                }
            }
        }
    }

    static int getNextIndex(MapLocation myLoc) {
        int currentIndex = outerWallLocations.indexOf(myLoc);
        System.out.println("CURRENT INDEX " + currentIndex);
        System.out.println("OUTER WALL LENGTH " + outerWallLocations.size());
        int nextIndex = 0;
        if (outerWallLocations.size() == 24) {
            if (movingCounterClockwise) {

                if (currentIndex < outerWallLocations.size() - 1) {
                    nextIndex = currentIndex + 1;
                    return nextIndex;
                }
                if (currentIndex == outerWallLocations.size() - 1) {
                    nextIndex = 0;
                    return nextIndex;
                }
            }
            if (movingClockwise) {

                if (currentIndex == 0) {
                    nextIndex = outerWallLocations.size()-1;
                    System.out.println("NEXT INDEX 1" + nextIndex);
                    return nextIndex;
                }
                if (currentIndex > 1) {
                    nextIndex = currentIndex - 1;
                    System.out.println("NEXT INDEX 2" + nextIndex);
                    return nextIndex;
                }
            }
        }
        if (outerWallLocations.size() < 24) {
            System.out.println("1");
            currentIndex = outerWallLocations.indexOf(myLoc);
            if (currentIndex == 0) {
                System.out.println("2");
                nextIndex = currentIndex + 1;
                movingClockwise = true;
                return nextIndex;
            }
            if (currentIndex == outerWallLocations.size() - 1) {
                System.out.println("3");
                nextIndex = outerWallLocations.size() - 2;
                movingClockwise = false;
                return nextIndex;
            }
            if (movingClockwise) {
                System.out.println("4");
                nextIndex = currentIndex + 1;
                return nextIndex;
            }
            if (!movingClockwise) {
                System.out.println("5");
                nextIndex = currentIndex - 1;
                return nextIndex;
            }
        }
        return nextIndex;
    }
}
