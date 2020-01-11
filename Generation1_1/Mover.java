package Generation1_1;

import battlecode.common.*;

import java.awt.*;
import java.util.*;

public class Mover extends RobotPlayer {

    static RobotController rc = RobotPlayer.rc;
    //private static final Direction[] directions = new Direction[] {Direction.NORTH, Direction.NORTH_EAST,
    //Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH,
    //Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    private static final Random rand = new Random();

    private static enum BugPathState {HUGRIGHT, HUGLEFT, NONE}

    ;

    private static BugPathState bugPathState;
    private static Direction wallDir;
    private static MapLocation[] towers;
    static PriorityQueue<Location_Que> my_prioritized_soup = new PriorityQueue<>();


    public static void goTo(MapLocation loc) throws GameActionException {
        myLoc = rc.getLocation();
        System.out.println("goTO");
        bugPathState = BugPathState.NONE;
        bugPath(loc);
    }

    public static void bugPath(MapLocation loc) throws GameActionException {
        Direction d = myLoc.directionTo(loc);
        MapLocation m = myLoc.add(d);
        System.out.println("bugPath");

        switch (bugPathState) {
            case NONE:
                //rc.setIndicatorString(0, "Direct");
                if (traversable(d)) {
                    tryToMove(d);
                } else {
                    startBugPath(loc);
                }
                break;
            case HUGRIGHT:
                // check if we can escape
                //rc.setIndicatorString(0, "HugRight");
                if (canEndBugPath(d)) {
                    bugPathState = BugPathState.NONE;
                } else {
                    Direction movingDirection = wallDir;

                    while (!traversable(movingDirection)) {
                        movingDirection = movingDirection.rotateLeft();
                    }

                    if (rc.isReady()) {
                        if (rc.canMove(movingDirection) && safeMove(myLoc.add(movingDirection))) {
                            rc.move(movingDirection);

                            if (isDiagonal(movingDirection)) {
                                wallDir = movingDirection.rotateRight().rotateRight().rotateRight();
                            } else {
                                wallDir = movingDirection.rotateRight().rotateRight();
                            }

                        } else {
                            bugPathState = BugPathState.HUGLEFT;
                        }
                    }
                }
                //rc.setIndicatorString(2, wallDir.toString());
                break;
            case HUGLEFT:
                //rc.setIndicatorString(0, "HugLeft");
                // check if we can escape
                if (canEndBugPath(d)) {
                    bugPathState = BugPathState.NONE;
                } else {
                    Direction movingDirection = wallDir;

                    while (!traversable(movingDirection)) {
                        movingDirection = movingDirection.rotateRight();
                    }

                    if (rc.isReady()) {
                        if (rc.canMove(movingDirection) && safeMove(myLoc.add(movingDirection))) {
                            rc.move(movingDirection);
                            if (isDiagonal(movingDirection)) {
                                wallDir = movingDirection.rotateLeft().rotateLeft().rotateLeft();
                            } else {
                                wallDir = movingDirection.rotateLeft().rotateLeft();
                            }
                        } else {
                            bugPathState = BugPathState.HUGRIGHT;
                        }
                    }
                }
                //rc.setIndicatorString(2, wallDir.toString());
                break;
        }
    }

    private static boolean canEndBugPath(Direction dir) throws GameActionException {
        if (!traversable(dir)) {
            return false;
        }
        Direction movingDirection = wallDir;
        switch (bugPathState) {
            case NONE:
                return true;
            case HUGLEFT:
                return isFrontMove(movingDirection.rotateRight().rotateRight(), dir);
            case HUGRIGHT:
                return isFrontMove(movingDirection.rotateLeft().rotateLeft(), dir);
        }
        return false;
    }

    public static boolean traversable(Direction d) throws GameActionException {
        return rc.canMove(d);
    }

    private static boolean isFrontMove(Direction moving, Direction dir) {
        if (moving.ordinal() == dir.ordinal()) {
            return true;
        }
        if (moving.rotateLeft().ordinal() == dir.ordinal()) {
            return true;
        }
        if (moving.rotateLeft().rotateLeft().ordinal() == dir.ordinal()) {
            return true;
        }
        if (moving.rotateRight().ordinal() == dir.ordinal()) {
            return true;
        }
        if (moving.rotateRight().rotateRight().ordinal() == dir.ordinal()) {
            return true;
        }
        return false;
    }

    private static void startBugPath(MapLocation loc) throws GameActionException {
        Direction d = myLoc.directionTo(loc);

        Direction hugLeftDir = d.rotateRight();
        Direction hugRightDir = d.rotateLeft();

        if (!rc.isReady())
            return;

        while (!traversable(hugLeftDir)) {
            hugLeftDir = hugLeftDir.rotateRight();
        }
        while (!traversable(hugRightDir)) {
            hugRightDir = hugRightDir.rotateLeft();
        }

        if (loc.distanceSquaredTo(myLoc.add(hugRightDir)) > loc.distanceSquaredTo(myLoc.add(hugLeftDir))) {
            // we greedily chose the closest move
            bugPathState = BugPathState.HUGLEFT;
            if (rc.canMove(hugLeftDir) && safeMove(myLoc.add(hugLeftDir))) {
                rc.move(hugLeftDir);
                // wall direction is different for diagonal moves
                if (isDiagonal(hugLeftDir)) {
                    wallDir = hugLeftDir.rotateLeft().rotateLeft().rotateLeft();
                } else {
                    wallDir = hugLeftDir.rotateLeft().rotateLeft();
                }
            } else {
                wallDir = d;
                bugPathState = BugPathState.HUGRIGHT;
            }

        } else {
            bugPathState = BugPathState.HUGRIGHT;
            if (rc.canMove(hugRightDir) && safeMove(myLoc.add(hugRightDir))) {
                rc.move(hugRightDir);
                if (isDiagonal(hugRightDir)) {
                    wallDir = hugRightDir.rotateRight().rotateRight().rotateRight();
                } else {
                    wallDir = hugRightDir.rotateRight().rotateRight();
                }
            } else {
                wallDir = d;
                bugPathState = BugPathState.HUGLEFT;
            }
        }


    }

    public static boolean safeToGoAround() {
        return true;
    }

    // returns true or false depending on if something is in the way
    public static boolean tryToMove(Direction preferred) throws GameActionException {
        myLoc = rc.getLocation();
        if (!rc.isReady()) {
            return false;
        }
        if (rc.canMove(preferred) && safeMove(myLoc.add(preferred))) {
            rc.move(preferred);
            return true;
        } else {
            Direction right = preferred.rotateRight();
            Direction left = preferred.rotateLeft();
            if (rand.nextBoolean()) {
                if (rc.canMove(right) && safeMove(myLoc.add(right))) {
                    rc.move(right);
                    return true;
                }
                if (rc.canMove(left) && safeMove(myLoc.add(left))) {
                    rc.move(left);
                    return true;
                }
            } else {
                if (rc.canMove(left) && safeMove(myLoc.add(left))) {
                    rc.move(left);
                    return true;
                }
                if (rc.canMove(right) && safeMove(myLoc.add(right))) {
                    rc.move(right);
                    return true;
                }
            }
            if (rand.nextBoolean()) {
                if (rc.canMove(right.rotateRight()) && safeMove(myLoc.add(right.rotateRight()))) {
                    rc.move(right.rotateRight());
                    return true;
                }
                if (rc.canMove(left.rotateLeft()) && safeMove(myLoc.add(left.rotateLeft()))) {
                    rc.move(left.rotateLeft());
                    return true;
                }
            }
            if (rc.canMove(left.rotateLeft()) && safeMove(myLoc.add(left.rotateLeft()))) {
                rc.move(left.rotateLeft());
                return true;
            }
            if (rc.canMove(right.rotateRight()) && safeMove(myLoc.add(right.rotateRight()))) {
                rc.move(right.rotateRight());
                return true;
            }
        }

        return false;
    }


    static int directionToInt(Direction d) {
        switch (d) {
            case NORTH:
                return 0;
            case NORTHEAST:
                return 1;
            case EAST:
                return 2;
            case SOUTHEAST:
                return 3;
            case SOUTH:
                return 4;
            case SOUTHWEST:
                return 5;
            case WEST:
                return 6;
            case NORTHWEST:
                return 7;
            default:
                return -1;
        }
    }

    public static void goToClosestSoup() throws GameActionException {
        myLoc = rc.getLocation();
        if (soupLoc != null) {
            if (soupLoc.isAdjacentTo(myLoc) && rc.senseSoup(soupLoc) > 0) {
                goingtoSoup = false;
                miningSoup = true;
                System.out.println("AT SOUP, Mining" + soupLoc);
            }
            if (rc.canSenseLocation(soupLoc) && rc.senseSoup(soupLoc) == 0) {
                soupLoc = null;
                goingtoSoup = true;
                miningSoup = false;
            }
        }
        if (soupLoc == null && goingtoSoup) {
            System.out.println("SOUPLOC SEARCH START");
            int x_min = -1;
            int x_max = 1;
            int y_min = -1;
            int y_max = 1;
            int radius = 8;
            MapLocation seach_center = myLoc;
            outerloop:
            for (int r = 1; r < radius; r++) {
                for (int b = y_min * r; b < y_max * r; b++) {
                    for (int a = x_min * r; a <= x_max * r; a++) {
                        MapLocation search_location = new MapLocation(seach_center.x + a, seach_center.y + b);

                        if (rc.canSenseLocation(search_location)) {
                            System.out.println("SOUP SEARCH " + search_location + "r " + r + " b " + b + "a " + a);
                            int soup = rc.senseSoup(search_location);
                            if (soup > 0) {
                                my_prioritized_soup.add(new Location_Que(search_location, r));
                                soupLoc = search_location;
                                System.out.println("SOUP AT " + soupLoc);
                                break outerloop;
                            }
                        }
                    }
                }
            }
        }

        System.out.println("GOING TO SOUP AT " + soupLoc);
        if (soupLoc != null) {
            if (rc.canSenseLocation(soupLoc) && myLoc.isAdjacentTo(soupLoc) && rc.senseSoup(soupLoc) > 0) {
                goingtoSoup = false;
                miningSoup = true;
            }

            if (goingtoSoup) {
                goTo(soupLoc);
            }
        }
    }


        public static MapLocation maximumDistanceFromHQ (MapLocation[]locs){
            MapLocation furthest = locs[0];
            MapLocation hq = hqLoc;
            int distance = furthest.distanceSquaredTo(hq);

            for (MapLocation loc : locs) {
                int newdist = loc.distanceSquaredTo(hq);
                if (newdist > distance) {
                    furthest = loc;
                    distance = newdist;
                }
            }
            return furthest;
        }

        public static boolean blocked (MapLocation m) throws GameActionException {
            myLoc = rc.getLocation();
            MapLocation current = myLoc.add(myLoc.directionTo(m));
            while (!current.equals(m)) {
                if (rc.isLocationOccupied(current)) {
                    return true;
                }
                current = current.add(current.directionTo(m));
            }

            return false;
        }

        private static boolean closeToTowers (MapLocation m){
            int TSqrange = GameConstants.NET_GUN_SHOOT_RADIUS_SQUARED;
            RobotInfo[] towers = rc.senseNearbyRobots(-1, enemyTeam);

            for (RobotInfo tower : towers) {
                if (tower.type == RobotType.NET_GUN) {
                    int range = myLoc.distanceSquaredTo(tower.location);
                    if (range <= TSqrange && myType == RobotType.DELIVERY_DRONE) {
                        return true;
                    }
                }
            }
            return false;
        }

        private static boolean safeMove (MapLocation m){
            return !closeToTowers(m);
        }

        public static boolean moveDrone (Direction preferred) throws GameActionException {
            myLoc = rc.getLocation();
            if (!rc.isReady()) {
                return false;
            }
            if (rc.canMove(preferred) && safeMove(myLoc.add(preferred))) {
                rc.move(preferred);
                return true;
            } else {
                Direction right = preferred.rotateRight();
                Direction left = preferred.rotateLeft();
                if (rand.nextBoolean()) {
                    if (rc.canMove(right) && safeMove(myLoc.add(right))) {
                        rc.move(right);
                        return true;
                    }
                    if (rc.canMove(left) && safeMove(myLoc.add(left))) {
                        rc.move(left);
                        return true;
                    }
                } else {
                    if (rc.canMove(left) && safeMove(myLoc.add(left))) {
                        rc.move(left);
                        return true;
                    }
                    if (rc.canMove(right) && safeMove(myLoc.add(right))) {
                        rc.move(right);
                        return true;
                    }
                }
                right = right.rotateRight();
                left = left.rotateLeft();
                if (rand.nextBoolean()) {
                    if (rc.canMove(right) && safeMove(myLoc.add(right))) {
                        rc.move(right);
                        return true;
                    }
                    if (rc.canMove(left) && safeMove(myLoc.add(left))) {
                        rc.move(left);
                        return true;
                    }
                } else {
                    if (rc.canMove(left) && safeMove(myLoc.add(left))) {
                        rc.move(left);
                        return true;
                    }
                    if (rc.canMove(right) && safeMove(myLoc.add(right))) {
                        rc.move(right);
                        return true;
                    }
                }
            }

            return false;
        }

        public static boolean isDiagonal (Direction direction){
            if (direction == Direction.NORTHEAST || direction == Direction.NORTHWEST || direction == Direction.SOUTHEAST || direction == Direction.SOUTHWEST) {
                return true;
            }
            return false;
        }


        public static MapLocation findClosestToSafe (MapLocation[]locs, MapLocation m) throws GameActionException {
            RobotInfo[] enemies = rc.senseNearbyRobots(35, rc.getTeam().opponent());
            MapLocation min = null;
            int min_distance = 99999999;
            for (MapLocation loc : locs) {
                int dist = m.distanceSquaredTo(loc);
                boolean acceptable = true;
                for (RobotInfo enemy : enemies) {
                    if (enemy.type == RobotType.DELIVERY_DRONE && (myType == RobotType.LANDSCAPER || myType == RobotType.MINER)) {
                        if (loc.distanceSquaredTo(enemy.location) <= GameConstants.NET_GUN_SHOOT_RADIUS_SQUARED) {
                            acceptable = false;
                        }
                    } else {
                        if (enemy.type == RobotType.HQ || enemy.type == RobotType.NET_GUN && loc.distanceSquaredTo(enemy.location) <= GameConstants.NET_GUN_SHOOT_RADIUS_SQUARED) {
                            acceptable = false;
                        }
                    }

                }
                if (dist < min_distance && safeMove(loc) && acceptable) {
                    min = loc;
                    min_distance = dist;
                }
            }
            return min;
        }


    }
