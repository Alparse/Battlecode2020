package Generation1_1;

import battlecode.common.*;
import gnu.trove.impl.sync.TSynchronizedShortByteMap;

import java.util.Random;


public class Bug1 extends RobotPlayer {
    static RobotController rc = RobotPlayer.rc;
    static Direction v=null;



    public static Direction BugGetNext(MapLocation goal) throws GameActionException {

        if (bugPathState == BugPathState.NONE) {
            System.out.println("STATE NONE 1 ");
            v=myLoc.directionTo(goal);
            if (!isPassable(v)) {
                obstacleDirection = v;
                v = boundaryFollow(v);
                lastBuggingDirection=v;
            }
            return v;
        }
        if (bugPathState == BugPathState.HUGLEFT || bugPathState == BugPathState.HUGRIGHT) {
            System.out.println("STATE HUGGING 1 "+bugPathState+" Last Dir "+lastBuggingDirection);
            myLoc=rc.getLocation();

            v = boundaryFollow(lastBuggingDirection);
            lastBuggingDirection=v;
            System.out.println(v);
            MapLocation next_step=myLoc.add(v);
            int distanceToGoal = myLoc.distanceSquaredTo(goal);
            int nextStepDistanceToGoal = myLoc.add(v).distanceSquaredTo(goal);
            if (nextStepDistanceToGoal < distanceToGoal) {
                bugPathState = BugPathState.NONE;
                lastBuggingDirection=null;
                v = myLoc.directionTo(goal);
                System.out.println(v);
                System.out.println("LINE Bug next 40");
                return v;
            }
            System.out.println("STATE HUGGING 1 "+bugPathState+v);
            System.out.println("LINE Bug next 44");
            return v;
        }
        System.out.println("LINE Bug next 45");
        return v;
    }

    public static boolean isPassable(Direction move_dir) throws GameActionException {

        if (!rc.canSenseLocation(myLoc.add(move_dir))){
            return false;
        }
        int destinationHeight = rc.senseElevation(myLoc.add(move_dir));
        boolean notFlooded = !rc.senseFlooding(myLoc.add(move_dir));
        boolean notTooHigh = !(Math.abs(destinationHeight - myHeight) > 3);
        boolean notOccupied=(rc.senseRobotAtLocation(myLoc.add(move_dir))==null);
        boolean notInTrain=!(trail.contains(myLoc.add(move_dir)));
        boolean onTheMap=rc.onTheMap(myLoc.add(move_dir));
        return notFlooded && notTooHigh && notOccupied&&notInTrain&&onTheMap;

    }

    public static Direction boundaryFollow(Direction v) throws GameActionException {


        if (!isPassable(v.rotateLeft())&&isPassable(v)&&bugPathState==BugPathState.HUGLEFT){

            System.out.println("1"+v);
            return v;
        }
        if (!isPassable(v.rotateRight())&&isPassable(v)&&bugPathState==BugPathState.HUGRIGHT){
            System.out.println("2");
            return v;
        }

        if(isPassable(v.rotateLeft())&&bugPathState==BugPathState.HUGLEFT){
            v=v.rotateLeft();
            bugPathState=BugPathState.HUGLEFT;
            System.out.println("3");
            return v;
        }
        if(isPassable(v.rotateRight())&&bugPathState==BugPathState.HUGRIGHT){
            v=v.rotateRight();
            bugPathState=BugPathState.HUGRIGHT;
            System.out.println("4");
            return v;
        }

        if (isPassable(v.rotateRight()) && (bugPathState == BugPathState.NONE || bugPathState == BugPathState.HUGLEFT)) {
            v = v.rotateRight();
            bugPathState = BugPathState.HUGLEFT;
            System.out.println(v);
            System.out.println("5");
            return v;
        }
        if (isPassable(v.rotateLeft()) && (bugPathState == BugPathState.NONE || bugPathState == BugPathState.HUGRIGHT)) {
            v = v.rotateLeft();
            bugPathState = BugPathState.HUGRIGHT;
            System.out.println(v);
            System.out.println("6");
            return v;
        }
        if (isPassable(v.rotateRight().rotateRight()) && (bugPathState == BugPathState.NONE || bugPathState == BugPathState.HUGLEFT)) {
            v = v.rotateRight().rotateRight();
            bugPathState = BugPathState.HUGLEFT;
            System.out.println("TEST "+v);
            System.out.println("7");
            return v;
        }
        if (isPassable(v.rotateLeft().rotateLeft()) && (bugPathState == BugPathState.NONE || bugPathState == BugPathState.HUGRIGHT)) {
            v = v.rotateLeft().rotateLeft();
            bugPathState = BugPathState.HUGRIGHT;
            System.out.println(v);
            System.out.println("8");
            return v;
        }
        if (isPassable((v.rotateRight().rotateRight().rotateRight())) && (bugPathState == BugPathState.NONE || bugPathState == BugPathState.HUGLEFT)) {
            v = v.rotateRight().rotateRight().rotateRight().rotateRight();
            bugPathState = BugPathState.HUGRIGHT;
            System.out.println(v);
            System.out.println("9");
            return v;
        }
        if (isPassable(v.rotateLeft().rotateLeft().rotateLeft()) && (bugPathState == BugPathState.NONE || bugPathState == BugPathState.HUGRIGHT)) {
            v = v.rotateLeft().rotateLeft().rotateLeft();
            bugPathState = BugPathState.HUGLEFT;
            System.out.println(v);
            System.out.println("10");
            return v;
        }
        if (isPassable(v.opposite()) && (bugPathState == BugPathState.NONE || bugPathState == BugPathState.HUGLEFT)) {
            v = v.opposite();
            bugPathState = BugPathState.HUGLEFT;
            System.out.println(v);
            System.out.println("11");
            return v;
        }
        if (trail.size()>4){
            trail.remove(trail.remove(0));
            System.out.println("11");
        }
        System.out.println("13" + v);
        if (trail.size()>0) {
            trail.remove(trail.remove(0));
        }
        return v;
    }
    static MapLocation random_explore(Direction explore_dir) throws GameActionException {
        int range =myType.sensorRadiusSquared;
        MapLocation new_loc=myLoc;
        new_loc=new_loc.add(explore_dir);

        if (!(rc.canSenseLocation(new_loc)&&rc.onTheMap(new_loc))){
            explore_dir=explore_dir.rotateRight(); }


        return new_loc;

    }

    static Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }




}