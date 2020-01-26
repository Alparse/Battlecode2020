package Generation1_1;

import battlecode.common.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

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

        if (rc.getRoundNum() > 250) {
            myState = landscaperState.TRAVELINGINNERWALL;
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
                            System.out.println(outerWallLocations);
                            Direction move_dir = myLoc.directionTo(outerWallLocations.get(nextIndex));
                            if (rc.senseFlooding(myLoc.add(move_dir))) {
                                myState = landscaperState.BUILDINGOUTERWALL;

                                break;
                            }
                            if (rc.canMove(move_dir) && !rc.senseFlooding(myLoc.add(move_dir))) {
                                rc.move(move_dir);
                                myState = landscaperState.BUILDINGOUTERWALL;
                                break;
                            }
                            if (Math.abs(rc.senseElevation(myLoc.add(move_dir))) > myHeight + 4 || Math.abs(rc.senseElevation(myLoc.add(move_dir))) < myHeight - 4) {

                                if (movingClockwise == true) {

                                    movingCounterClockwise = true;
                                    movingClockwise = false;
                                    myState = landscaperState.MOVINGONOUTERWALL;
                                    break;
                                }
                                if (movingClockwise == false) {

                                    movingCounterClockwise = false;
                                    movingClockwise = true;
                                    myState = landscaperState.MOVINGONOUTERWALL;
                                    break;
                                }

                            }
                            if (Math.abs(rc.senseElevation(myLoc.add(move_dir))) == myHeight + 4 || Math.abs(rc.senseElevation(myLoc.add(move_dir))) == myHeight - 4) {
                                myState = landscaperState.BUILDINGOUTERWALL;
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
                        if (movingClockwise && !rc.onTheMap(outerWallLocations.get(nextIndex))) {
                            movingClockwise = false;
                            movingCounterClockwise = true;
                            nextIndex = getNextIndex(myLoc);

                        }
                        if (movingCounterClockwise && !rc.onTheMap(outerWallLocations.get(nextIndex))) {
                            movingClockwise = true;
                            movingCounterClockwise = false;
                            nextIndex = getNextIndex(myLoc);

                        }

                        //nextIndex = getNextIndex(myLoc);


                        if (rc.senseFlooding((outerWallLocations.get(nextIndex)))) {

                            if (rc.senseElevation(myLoc.add(myLoc.directionTo(outerWallLocations.get(nextIndex)))) < -50) {

                                myState = landscaperState.MOVINGINNERWALL;
                                break;
                            }
                            if (rc.canDepositDirt(myLoc.directionTo(outerWallLocations.get(nextIndex)))) {
                                if (rc.isReady()) {
                                    rc.depositDirt(myLoc.directionTo(outerWallLocations.get(nextIndex)));
                                    myState = landscaperState.BUILDINGOUTERWALL;
                                    break;
                                }
                            }
                        }
                        if (rc.senseFlooding(myLoc.add(myLoc.directionTo(hqLoc)))) {
                            if (rc.senseElevation(myLoc.add(myLoc.directionTo(hqLoc))) < -50) {

                                myState = landscaperState.MOVINGINNERWALL;
                                break;
                            }

                            if (rc.canDepositDirt(myLoc.directionTo(hqLoc))) {
                                if (rc.isReady()) {
                                    rc.depositDirt(myLoc.directionTo(hqLoc));
                                    myState = landscaperState.BUILDINGOUTERWALL;
                                    break;
                                }
                            }

                        }


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
                            if (rc.getRoundNum()>600){
                                myState=landscaperState.BUILDINGOUTERWALL;
                            }
                        }

                        break;

                    case TRAVELINGINNERWALL:
                        System.out.println(myState);
                        MapLocation target_location = findBestInnerWallLocation(myLoc);
                        if(rc.getRoundNum()<600) {

                            Direction move_dir = Bug1.BugGetNext(target_location);
                            Utility.makeMove(move_dir);
                        }
                            if (myLoc == target_location || rc.getRoundNum() > 400) {
                                if (rc.getDirtCarrying() == 0) {
                                    getDirt(myLoc);
                                }
                                if (rc.getDirtCarrying() > 0) {
                                    if (rc.isReady()) {
                                        for(Direction dir:directions){
                                            if (myHeight>rc.senseElevation(myLoc.add(dir))&&innerWallLocations.contains(myLoc.add(dir))){
                                                if (rc.canDepositDirt(dir)) {
                                                    rc.depositDirt(dir);
                                                    break;
                                            }
                                        }
                                        }
                                        if (rc.canDepositDirt(Direction.CENTER)) {
                                            rc.depositDirt(Direction.CENTER);
                                        }
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

        myLoc = rc.getLocation();
        if (rc.isReady()) {
            for (MapLocation digLoc : digLocations) {

                if (myLoc.isAdjacentTo(digLoc)) {

                    if (rc.canDigDirt(myLoc.directionTo(digLoc))) {
                        rc.digDirt(myLoc.directionTo(digLoc));

                        return;
                    }
                }
            }
        }
    }

    static int getNextIndex(MapLocation myLoc) {
        myLoc = rc.getLocation();
        int currentIndex = outerWallLocations.indexOf(myLoc);


        int nextIndex = 0;
        if (outerWallLocations.size() <= 24) {
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
                    nextIndex = outerWallLocations.size() - 1;

                    return nextIndex;
                }
                if (currentIndex > 1) {
                    nextIndex = currentIndex - 1;

                    return nextIndex;
                }
            }
        }
        return nextIndex;
    }
    static MapLocation findBestInnerWallLocation(MapLocation myLoc) throws GameActionException {
        if (rc.senseRobotAtLocation(hqLoc.add(Direction.NORTHWEST)) == null) {
            return hqLoc.add(Direction.NORTHWEST);

        }
        if (rc.senseRobotAtLocation(hqLoc.add(Direction.SOUTHWEST)) == null) {
            return hqLoc.add(Direction.SOUTHWEST);

        }
        if (rc.senseRobotAtLocation(hqLoc.add(Direction.NORTHEAST)) == null) {
            return hqLoc.add(Direction.NORTHEAST);

        }
        if (rc.senseRobotAtLocation(hqLoc.add(Direction.SOUTHEAST)) == null) {
            return hqLoc.add(Direction.SOUTHEAST);
        }
        if (rc.senseRobotAtLocation(hqLoc.add(Direction.SOUTH)) == null) {
            return hqLoc.add(Direction.SOUTH);

        }
        if (rc.senseRobotAtLocation(hqLoc.add(Direction.WEST)) == null) {
            return hqLoc.add(Direction.WEST);

        }
        if (rc.senseRobotAtLocation(hqLoc.add(Direction.NORTH)) == null) {
            return hqLoc.add(Direction.NORTH);

        }
        if (rc.senseRobotAtLocation(hqLoc.add(Direction.EAST)) == null) {
            return hqLoc.add(Direction.EAST);
        }
        return myLoc;
    }
}
