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
    public static void sendMinerJob(int robotID, int job) throws GameActionException {
        // 0= Miner; 1= Construct Design Center; 2== Construct Fulfillment Center ; 3 = Construct Refinery; 4:Construct Netgun

        int teamSecret = rc.getRoundNum() * 2 + 3333;
        int[] message = new int[7];
        message[0] = teamSecret;
        message[1] = 0;
        message[2] = 0;
        message[3] = robotID;
        message[4] = job;
        if (rc.canSubmitTransaction(message, 3)) {
            rc.submitTransaction(message, 3);
            System.out.println("BLOCKCHAIN SEND JOB MESSAGE "+job);
        }
    }

    public static int getMinerJobFromBlockchain() throws GameActionException {
        int job=0;
        System.out.println("BLOCKCHAIN GET JOB MESSAGE");
        for (int i = 1; i < rc.getRoundNum(); i++) {
            for (Transaction tx : rc.getBlock(i)) {
                int[] mess = tx.getMessage();
                if (mess[0] == 3333 + i * 2 && mess[1] == 0) {
                    System.out.println("GOT JOB MESSAGE");
                    System.out.println(Arrays.toString(mess));
                    if(mess[3]==rc.getID()){
                        job=mess[4];
                        return job;
                    }
                }
            }
        }
        return job;
    }
}