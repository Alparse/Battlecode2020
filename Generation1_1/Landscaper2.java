package Generation1_1;

import battlecode.common.*;

import java.util.ArrayList;

public class Landscaper2 extends RobotPlayer {

    enum landscaperState {START,TRAVELINGOUTERWALL, TRAVELINGINNERWALL, TERRAFORMING, MOVINGONOUTERWALL, BUILDINGOUTERWALL, MANNINGOUTERWALL, BUILDINGINNERWALL,MOVINGINNERWALL, MANNINGINNERWALL, RESCUINGBUILDING, ATTACKINGBUILDING}

    static landscaperState myState = landscaperState.TRAVELINGOUTERWALL;

    static RobotController rc = RobotPlayer.rc;
    static int landScaperJob = 99;
    static ArrayList<MapLocation> digLocations = null;
    static ArrayList<MapLocation> outerWallLocations = null;
    static MapLocation lastLoc = null;
    static boolean movedOnWall = false;

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


        while (true) {
            try {
                myLoc = rc.getLocation();
                myHeight = rc.senseElevation(myLoc);
                friendlyRobots = rc.senseNearbyRobots(-1, myTeam);
                enemyRobots = rc.senseNearbyRobots(-1, enemyTeam);
                Utility.friendlyRobotScan();
                Utility.enemyRobotScan();
                if(rc.getRoundNum()>500) {
                    if (myLoc.isAdjacentTo(hqLoc)) {
                        myState = landscaperState.BUILDINGINNERWALL;
                    }
                }

                switch (myState) {

                    case BUILDINGINNERWALL:

                        if (rc.getDirtCarrying() == 0) {
                            getDirt(myLoc);
                        }
                        if (rc.getDirtCarrying() > 0) {
                            if (rc.isReady()) {
                                if (rc.canDepositDirt(Direction.CENTER)) {
                                    rc.depositDirt(Direction.CENTER);
                                }
                            }
                        }
                        break;



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
                        System.out.println(myState);
                        if (!outerWallLocations.contains(myLoc)) {
                            myState = landscaperState.TRAVELINGOUTERWALL;
                        }
                        if (outerWallLocations.contains(myLoc)) {
                            int currentIndex = outerWallLocations.indexOf(myLoc);
                            int nextIndex = 0;
                            if (currentIndex < outerWallLocations.size() - 1) {
                                nextIndex = currentIndex + 1;
                            }
                            if (currentIndex == outerWallLocations.size() - 1) {
                                nextIndex = 0;
                            }
                            if (Math.abs(rc.senseElevation(outerWallLocations.get(nextIndex)) - myHeight) > 3) {
                                myState = landscaperState.BUILDINGOUTERWALL;
                                break;
                            }
                            Direction move_dir = myLoc.directionTo(outerWallLocations.get(nextIndex));
                            if (rc.canMove(move_dir) && !rc.senseFlooding(myLoc.add(move_dir))) {
                                rc.move(move_dir);
                                myState = landscaperState.BUILDINGOUTERWALL;
                            }

                        }
                        break;


                    case BUILDINGOUTERWALL:
                        System.out.println(myState);
                        //check next step;
                        if (rc.getDirtCarrying() == 0) {
                            getDirt(myLoc);
                        }
                        int currentIndex = outerWallLocations.indexOf(myLoc);
                        int nextIndex = 0;
                        if (currentIndex < outerWallLocations.size() - 1) {
                            nextIndex = currentIndex + 1;
                        }
                        if (currentIndex == outerWallLocations.size() - 1) {
                            nextIndex = 0;
                        }

                        if (rc.senseElevation(outerWallLocations.get(nextIndex)) - myHeight > 3) {
                            //add to my location till ok
                            if (rc.canDepositDirt(Direction.CENTER)) {
                                if (rc.isReady()) {
                                    rc.depositDirt(Direction.CENTER);
                                    myState = landscaperState.MOVINGONOUTERWALL;
                                }
                            }
                        }
                        if (rc.senseElevation(outerWallLocations.get(nextIndex)) - myHeight <=3 ) {
                            //add to next location till ok
                            if (rc.canDepositDirt(myLoc.directionTo(outerWallLocations.get(nextIndex)))) {
                                if (rc.isReady()) {
                                    rc.depositDirt(myLoc.directionTo(outerWallLocations.get(nextIndex)));
                                    myState = landscaperState.MOVINGONOUTERWALL;
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
}
