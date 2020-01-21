package Generation1_1;

import battlecode.common.*;
import gnu.trove.impl.sync.TSynchronizedShortByteMap;

import java.util.Random;
import java.util.zip.DeflaterInputStream;


public class Bug1 extends RobotPlayer {
    static RobotController rc = RobotPlayer.rc;
    static Direction v = null;


    public static Direction BugGetNext(MapLocation goal) throws GameActionException {
        myLoc = rc.getLocation();
        myHeight = rc.senseElevation(myLoc);

        if (bugPathState == BugPathState.NONE) {
            System.out.println("STARTING BUG GET NEXT>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
            v = myLoc.directionTo(goal);
            lastBugEntry = bugEntry;
            bugEntry = myLoc;
            if (!isPassable(v)) {
                obstacleDirection = v;
                v = boundaryFollow(v, goal);
                lastBuggingDirection = v;
            }

            return v;
        }
        if (bugPathState == BugPathState.HUGLEFT || bugPathState == BugPathState.HUGRIGHT) {
            System.out.println("STATE HUGGING 1 " + bugPathState + " Last Dir " + lastBuggingDirection);
            myLoc = rc.getLocation();

            v = boundaryFollow(lastBuggingDirection, goal);
            lastBuggingDirection = v;
            System.out.println(v);
            MapLocation next_step = myLoc.add(v);
            if (isPassable(next_step.directionTo(goal)) && lastBugEntry != next_step) {
                bugPathState = BugPathState.NONE;
                lastBuggingDirection = null;
                return v;
            }

            System.out.println("STATE HUGGING 1 " + bugPathState + v);
            System.out.println("LINE Bug next 44 " + v);
            return v;
        }
        System.out.println("LINE Bug next 45");
        return v;
    }

    public static boolean isPassable(Direction move_dir) throws GameActionException {

        if (!rc.canSenseLocation(myLoc.add(move_dir))) {
            return false;
        }
        int destinationHeight = rc.senseElevation(myLoc.add(move_dir));
        boolean notFlooded = !rc.senseFlooding(myLoc.add(move_dir));
        boolean notTooHigh = !(Math.abs(destinationHeight - myHeight) > 3);
        boolean notOccupied = (rc.senseRobotAtLocation(myLoc.add(move_dir)) == null);
        boolean notInTrain = !(trail.contains(myLoc.add(move_dir)));
        //System.out.println("NOT TRAIN +"+notInTrain +"direction "+move_dir);
        boolean onTheMap = rc.onTheMap(myLoc.add(move_dir));
        return notFlooded && notTooHigh && notOccupied && notInTrain && onTheMap;

    }

    public static boolean isPassableDiagCheck(MapLocation checked_loc, Direction goal_dir) throws GameActionException {

        if (!rc.canSenseLocation(checked_loc)) {
            return false;
        }
        int destinationHeight = rc.senseElevation(checked_loc.add(goal_dir));
        boolean notFlooded = !rc.senseFlooding(checked_loc.add(goal_dir));
        boolean notTooHigh = !(Math.abs(rc.senseElevation(checked_loc) - rc.senseElevation(checked_loc.add(goal_dir))) > 3);
        boolean notOccupied = (rc.senseRobotAtLocation(checked_loc.add(goal_dir)) == null);
        boolean notInTrain = !(trail.contains(checked_loc.add(goal_dir)));

        boolean onTheMap = rc.onTheMap(checked_loc.add(goal_dir));
        //System.out.println("NOT TRAIN +"+notInTrain);
        return notFlooded && notTooHigh && notOccupied && notInTrain && onTheMap;

    }

    public static Direction boundaryFollow(Direction v, MapLocation goal) throws GameActionException {

        System.out.println("START V " + v);
        if (v == Direction.NORTHEAST || v == Direction.NORTHWEST || v == Direction.SOUTHEAST || v == Direction.SOUTHWEST) {
            if (bugPathState == BugPathState.HUGLEFT) {
                MapLocation check_loc = myLoc.add(v.rotateRight());
                if (isPassable(myLoc.directionTo(check_loc))) {
                    if (isPassableDiagCheck(check_loc, check_loc.directionTo(goal)) && isPassable(myLoc.directionTo(myLoc.add(v)))) {
                        v = v.rotateRight();
                        System.out.println("DIAGONAL CHECK PASSED " + v);
                        return v;
                    }

                }
            }
            if (bugPathState == BugPathState.HUGRIGHT) {
                MapLocation check_loc = myLoc.add(v.rotateLeft());
                if (isPassable(myLoc.directionTo(check_loc))) {
                    if (isPassableDiagCheck(check_loc, check_loc.directionTo(goal)) && isPassable(myLoc.directionTo(myLoc.add(v)))) {
                        v = v.rotateLeft();
                        System.out.println("DIAGONAL CHECK PASSED " + v);
                        return v;
                    }

                }
            }
        }
        if (!isPassable(v)||isPassable(v)) {


            if (!isPassable(v.rotateLeft()) && isPassable(v) && bugPathState == BugPathState.HUGLEFT) {

                System.out.println("1" + v);
                return v;
            }
            if (!isPassable(v.rotateRight()) && isPassable(v) && bugPathState == BugPathState.HUGRIGHT) {
                System.out.println("2" + v);
                return v;
            }

            if (isPassable(v.rotateLeft()) && bugPathState == BugPathState.HUGLEFT) {
                v = v.rotateLeft();
                bugPathState = BugPathState.HUGLEFT;
                System.out.println("3" + v);
                return v;
            }
            if (isPassable(v.rotateRight()) && bugPathState == BugPathState.HUGRIGHT) {
                v = v.rotateRight();
                bugPathState = BugPathState.HUGRIGHT;
                System.out.println("4" + v);
                return v;
            }

            if (isPassable(v.rotateRight()) && (bugPathState == BugPathState.NONE || bugPathState == BugPathState.HUGLEFT)) {
                v = v.rotateRight();
                bugPathState = BugPathState.HUGLEFT;
                System.out.println(v);
                System.out.println("5" + v);
                return v;
            }
            if (isPassable(v.rotateLeft()) && (bugPathState == BugPathState.NONE || bugPathState == BugPathState.HUGRIGHT)) {
                v = v.rotateLeft();
                bugPathState = BugPathState.HUGRIGHT;
                System.out.println(v);
                System.out.println("6" + v);
                return v;
            }
            if (isPassable(v.rotateRight().rotateRight()) && (bugPathState == BugPathState.NONE || bugPathState == BugPathState.HUGLEFT)) {
                v = v.rotateRight().rotateRight();
                bugPathState = BugPathState.HUGLEFT;
                System.out.println("TEST " + v);
                System.out.println("7" + v);
                return v;
            }
            if (isPassable(v.rotateLeft().rotateLeft()) && (bugPathState == BugPathState.NONE || bugPathState == BugPathState.HUGRIGHT)) {
                v = v.rotateLeft().rotateLeft();
                bugPathState = BugPathState.HUGRIGHT;
                System.out.println(v);
                System.out.println("8" + v);
                return v;
            }
            if (isPassable((v.rotateRight().rotateRight().rotateRight())) && (bugPathState == BugPathState.NONE || bugPathState == BugPathState.HUGLEFT)) {
                v = v.rotateRight().rotateRight().rotateRight();
                bugPathState = BugPathState.HUGRIGHT;
                System.out.println(v);
                System.out.println("9" + v);
                return v;
            }
            if (isPassable((v.rotateRight().rotateRight().rotateRight().rotateRight())) && (bugPathState == BugPathState.NONE || bugPathState == BugPathState.HUGLEFT)) {
                v = v.rotateRight().rotateRight().rotateRight().rotateRight();
                bugPathState = BugPathState.HUGRIGHT;
                System.out.println(v);
                System.out.println("9" + v);
                return v;
            }
            if (isPassable(v.rotateLeft().rotateLeft().rotateLeft()) && (bugPathState == BugPathState.NONE || bugPathState == BugPathState.HUGRIGHT)) {
                v = v.rotateLeft().rotateLeft().rotateLeft();
                bugPathState = BugPathState.HUGLEFT;
                System.out.println(v);
                System.out.println("10" + v);
                return v;
            }
            if (isPassable(v.rotateLeft().rotateLeft().rotateLeft().rotateLeft()) && (bugPathState == BugPathState.NONE || bugPathState == BugPathState.HUGRIGHT)) {
                v = v.rotateLeft().rotateLeft().rotateLeft();
                bugPathState = BugPathState.HUGLEFT;
                System.out.println(v);
                System.out.println("10" + v);
                return v;
            }
            if (isPassable(v.rotateLeft().rotateLeft().rotateLeft().rotateLeft().rotateLeft()) && (bugPathState == BugPathState.NONE || bugPathState == BugPathState.HUGRIGHT)) {
                v = v.rotateLeft().rotateLeft().rotateLeft().rotateLeft().rotateLeft();
                bugPathState = BugPathState.HUGLEFT;
                System.out.println(v);
                System.out.println("10" + v);
                return v;
            }
            if (isPassable(v.rotateLeft().rotateLeft().rotateLeft().rotateLeft().rotateLeft().rotateLeft()) && (bugPathState == BugPathState.NONE || bugPathState == BugPathState.HUGRIGHT)) {
                v = v.rotateLeft().rotateLeft().rotateLeft().rotateLeft().rotateLeft().rotateLeft();
                bugPathState = BugPathState.HUGLEFT;
                System.out.println(v);
                System.out.println("10" + v);
                return v;
            }
            if (isPassable(v.opposite()) && (bugPathState == BugPathState.NONE || bugPathState == BugPathState.HUGLEFT)) {
                v = v.opposite();
                bugPathState = BugPathState.HUGLEFT;
                System.out.println(v);
                System.out.println("11" + v);
                return v;
            }
            for (Direction d : directions) {
                if (rc.canMove(d) && !rc.senseFlooding(myLoc.add(d))) {
                    System.out.println("NOT PASSABLE ESCAPE" + d);
                    bugPathState = BugPathState.NONE;
                    return d;
        }
        //if (trail.size()>6){
        //trail.remove(trail.remove(0));
        //System.out.println("12");
        // }
        System.out.println("13" + v);
        System.out.println("NOT PASSABLE " + v);

            }
        }
        if (isPassable(v)) {
            return v;
        }else{
            return Direction.CENTER;
        }
    }

    static MapLocation random_explore(Direction explore_dir) throws GameActionException {
        int range = myType.sensorRadiusSquared;
        MapLocation new_loc = myLoc;
        new_loc = new_loc.add(explore_dir);

        if (!(rc.canSenseLocation(new_loc) && rc.onTheMap(new_loc))) {
            explore_dir = explore_dir.rotateRight();
        }


        return new_loc;

    }

    static Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }


}