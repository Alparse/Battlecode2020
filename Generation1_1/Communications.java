package Generation1_1;

import battlecode.common.*;

import java.util.Arrays;


public class Communications extends RobotPlayer {


    static final String[] messageType = {"HQ loc",};

    public static void sendHqLoc(MapLocation loc) throws GameActionException {

        int teamSecret = rc.getRoundNum() * 2 + 3333;
        int[] message = new int[7];
        message[0] = teamSecret;
        message[1] = 0;
        message[2] = loc.x;
        message[3] = loc.y;
        if (rc.canSubmitTransaction(message, 3)) {
            rc.submitTransaction(message, 3);
        }
    }

    public static MapLocation getHqLocFromBlockchain() throws GameActionException {
        for (int i = 1; i < rc.getRoundNum(); i++) {
            System.out.println("BLOCKCHAIN");
            for (Transaction tx : rc.getBlock(i)) {
                int[] mess = tx.getMessage();
                if (mess[0] == 3333 + i * 2 && mess[1] == 0) {
                    System.out.println("GOT MESSAGE");
                    System.out.println(Arrays.toString(mess));
                    return new MapLocation(mess[2], mess[3]);
                }
            }
        }
        return null;
    }
}