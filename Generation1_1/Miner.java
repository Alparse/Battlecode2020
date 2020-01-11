package Generation1_1;

import battlecode.common.*;

import java.awt.*;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;


public class Miner extends RobotPlayer {
    static RobotController rc = RobotPlayer.rc;
    static Map<MapLocation, Integer> soup_map = null;
    static PriorityQueue<Location_Que> my_prioritized_soup = new PriorityQueue<>();


    static void runMiner() throws GameActionException {
        myLoc=rc.getLocation();
        //goingToSoup
        //Mining Soup
        //Returning to HQ
        //Refining Soup

        if (goingtoSoup) {
            Mover.goToClosestSoup();
        }
        if (miningSoup) {
            mine_Soup(soupLoc);
        }
        if (returningSoup) {
            if (myLoc.isAdjacentTo(hqLoc)) {
                refiningSoup = true;
                returningSoup = false;
            }
            Mover.goTo(hqLoc);
        }
        if (refiningSoup) {
            refine_Soup(hqLoc);
        }
    }

    static void mine_Soup(MapLocation soupLoc) throws GameActionException {
        System.out.println("MINING SOUP");
        if (rc.getSoupCarrying() == RobotType.MINER.soupLimit) {
            returningSoup = true;
            miningSoup = false;
        }
        if (rc.senseSoup(soupLoc) == 0 && rc.getSoupCarrying() < RobotType.MINER.soupLimit) {
            goingtoSoup = true;
            miningSoup = false;
        }
        if (rc.canMineSoup(myLoc.directionTo(soupLoc)) && !returningSoup) {
            miningSoup = true;
            returningSoup = false;
            if (rc.isReady()) {
                rc.mineSoup(myLoc.directionTo(soupLoc));
            }
        }
    }

    static void refine_Soup(MapLocation hqLoc) throws GameActionException {
        System.out.println("REFINING SOUP" + rc.getSoupCarrying());
        RobotInfo[] nearby_Friendlies = rc.senseNearbyRobots(4, myTeam);
        System.out.println(myType);
        for (RobotInfo r : nearby_Friendlies) {
            if (myType == RobotType.MINER) {
                if (r.type == RobotType.HQ || r.type == RobotType.REFINERY) {
                    hqLoc = r.location;
                    System.out.println("HQ LOC "+hqLoc);
                    System.out.println("My LOC "+myLoc);
                }
            }
        }
        if (rc.isReady()&&rc.canDepositSoup(myLoc.directionTo(hqLoc))){
            rc.depositSoup(myLoc.directionTo(hqLoc),RobotType.MINER.soupLimit);
            System.out.println("TRYING TO DEPOSIT SOUP");
            }
        if (rc.getSoupCarrying() == 0) {
            refiningSoup = false;
            goingtoSoup = true;
        }
    }
}


