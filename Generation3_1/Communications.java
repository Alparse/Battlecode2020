package Generation3_1;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Transaction;

import java.util.Arrays;


public class Communications extends RobotPlayer {


    static final String[] messageType = {"HQ loc",};

    public static void sendHqLoc(MapLocation loc, int bid) throws GameActionException {

        int teamSecret = rc.getRoundNum() * 2 + 3333;
        int[] message = new int[7];
        message[0] = teamSecret;
        message[1] = 0;
        message[2] = 0;
        message[3] = loc.x;
        message[4] = loc.y;
        message[5] = 0;
        message[6] = rc.getID();
        if (rc.canSubmitTransaction(message, bid)) {
            rc.submitTransaction(message, bid);
            messageQue.add(new Message_Que(0, bid, message[0], message[1], message[2], message[3], message[4], message[5], message[6]));
        }
    }
    public static void sendEnemyHqLoc(MapLocation loc, int bid) throws GameActionException {

        int teamSecret = rc.getRoundNum() * 2 + 3333;
        int[] message = new int[7];
        message[0] = teamSecret;
        message[1] = 2;
        message[2] = 0;
        message[3] = loc.x;
        message[4] = loc.y;
        message[5] = 0;
        message[6] = rc.getID();
        if (rc.canSubmitTransaction(message, bid)) {
            rc.submitTransaction(message, bid);
            messageQue.add(new Message_Que(0, bid, message[0], message[1], message[2], message[3], message[4], message[5], message[6]));
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
                    return new MapLocation(mess[3], mess[4]);
                }
            }
        }
        return null;
    }
    public static MapLocation getEnemyHqLocFromBlockchain() throws GameActionException {
        for (int i = 1; i < rc.getRoundNum(); i++) {
            System.out.println("BLOCKCHAIN");
            for (Transaction tx : rc.getBlock(i)) {
                int[] mess = tx.getMessage();
                if (mess[0] == 3333 + i * 2 && mess[1] == 2) {
                    System.out.println("GOT MESSAGE ENEMY HQ");
                    System.out.println(Arrays.toString(mess));
                    return new MapLocation(mess[3], mess[4]);
                }
            }
        }
        return null;
    }


    public static void sendMinerJob(int robotID, int job, int bid) throws GameActionException {
        // 0= Miner; 1= Construct Design Center; 2== Construct Fulfillment Center ; 3 = Construct Refinery; 4:Construct Netgun

        int teamSecret = rc.getRoundNum() * 2 + 3333;
        int[] message = new int[7];
        message[0] = teamSecret;
        message[1] = 10;
        message[2] = robotID;
        message[3] = job;
        message[4] = 0;
        message[5] = 0;
        message[6] = rc.getID();
        if (rc.canSubmitTransaction(message, bid)) {
            rc.submitTransaction(message, bid);
            messageQue.add(new Message_Que(0, bid, message[0], message[1], message[2], message[3], message[4], message[5], message[6]));
            System.out.println("BLOCKCHAIN SEND MINER JOB MESSAGE " + job);
        }
    }
    public static void sendLandScaperJob(int robotID, int job, int bid) throws GameActionException {
        // 0= Miner; 1= Construct Design Center; 2== Construct Fulfillment Center ; 3 = Construct Refinery; 4:Construct Netgun

        int teamSecret = rc.getRoundNum() * 2 + 3333;
        int[] message = new int[7];
        message[0] = teamSecret;
        message[1] = 20;
        message[2] = robotID;
        message[3] = job;
        message[4] = 0;
        message[5] = 0;
        message[6] = rc.getID();
        if (rc.canSubmitTransaction(message, bid)) {
            rc.submitTransaction(message, bid);
            messageQue.add(new Message_Que(0, bid, message[0], message[1], message[2], message[3], message[4], message[5], message[6]));
            System.out.println("BLOCKCHAIN SEND LANDSCAPER JOB MESSAGE " + job);
        }
    }

    public static int getMinerJobFromBlockchain() throws GameActionException {
        int job = 0;
        System.out.println("BLOCKCHAIN GET JOB MESSAGE");
        for (int i = 1; i < rc.getRoundNum(); i++) {
            for (Transaction tx : rc.getBlock(i)) {
                int[] mess = tx.getMessage();
                if (mess[0] == 3333 + i * 2 && mess[1] == 10) {
                    System.out.println("GOT JOB MESSAGE MINER");
                    System.out.println(Arrays.toString(mess));
                    if (mess[2] == rc.getID()) {
                        job = mess[3];
                        return job;
                    }
                }
            }
        }
        return job;
    }
    public static int getLandScaperFromBlockchain() throws GameActionException {
        int job = 0;
        System.out.println("BLOCKCHAIN GET JOB MESSAGE");
        for (int i = 1; i < rc.getRoundNum(); i++) {
            for (Transaction tx : rc.getBlock(i)) {
                int[] mess = tx.getMessage();
                if (mess[0] == 3333 + i * 2 && mess[1] == 20) {
                    System.out.println("GOT JOB MESSAGE LANDSCAPER");
                    System.out.println(Arrays.toString(mess));
                    if (mess[2] == rc.getID()) {
                        job = mess[3];
                        return job;
                    }
                }
            }
        }
        return job;
    }

    public static void checkMessagesQue() throws GameActionException {
        if (messageQue.size()>0) {
            for (int m=0; m<messageQue.size();m++) {
                if (messageQue.get(m).getMessage6() == rc.getID()) {
                    for (int i = 1; i < rc.getRoundNum(); i++) {
                        for (Transaction tx : rc.getBlock(i)) {
                            int[] mess = tx.getMessage();
                            if (mess[0] == messageQue.get(m).getMessage0() && mess[6] == messageQue.get(m).getMessage6()) {
                                System.out.println("MESSAGE IN BLOCK CHAIN, Removing from Que");
                                messageQue.remove(m);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void clearMessageQue() throws GameActionException {
        if (messageQue.size() > 0) {
            for (int m=0; m<messageQue.size();m++) {
                int[] message = new int[7];
                messageQue.get(m).setBid(messageQue.get(m).getBid()*2);
                message[0] = messageQue.get(m).getMessage0();
                message[1] = messageQue.get(m).getMessage1();
                message[2] = messageQue.get(m).getMessage2();
                message[3] = messageQue.get(m).getMessage3();
                message[4] = messageQue.get(m).getMessage4();
                message[5] = messageQue.get(m).getMessage5();
                message[6] = messageQue.get(m).getMessage6();
                if (rc.canSubmitTransaction(message, messageQue.get(m).getBid())) {
                    rc.submitTransaction(message, messageQue.get(m).getBid());
                }

            }
        }
    }
    public static void constructionStatus(int bid,int design_center,int fulfillment_center,int vaporatorsBuilt) throws GameActionException {

        int teamSecret = rc.getRoundNum() * 2 + 3333;
        int[] message = new int[7];
        message[0] = teamSecret;
        message[1] = 30;
        message[2] = design_center;
        message[3] = fulfillment_center;
        message[4] = vaporatorsBuilt;
        message[5] = 0;
        message[6] = rc.getID();
        if (rc.canSubmitTransaction(message, bid)) {
            rc.submitTransaction(message, bid);
            messageQue.add(new Message_Que(0, bid, message[0], message[1], message[2], message[3], message[4], message[5], message[6]));
        }
    }
    public static void getConstructionStatus() throws GameActionException {
        for (int i = 1; i < rc.getRoundNum(); i++) {
            for (Transaction tx : rc.getBlock(i)) {
                int[] mess = tx.getMessage();
                if (mess[0] == 3333 + i * 2 && mess[1] == 30) {

                    System.out.println(Arrays.toString(mess));
                    design_centerBuilt=mess[2];
                    fulfillment_centerBuilt=mess[3];
                    vaporatorsBuilt=mess[4];
                }
            }
        }
    }
    public static void HQButtonedUp(int bid,int HQbuttonedup) throws GameActionException {

        int teamSecret = rc.getRoundNum() * 2 + 3333;
        int[] message = new int[7];
        message[0] = teamSecret;
        message[1] = 40;
        message[2] = HQbuttonedup;
        message[3] = 0;
        message[4] = 0;
        message[5] = 0;
        message[6] = rc.getID();
        if (rc.canSubmitTransaction(message, bid)) {
            rc.submitTransaction(message, bid);
            messageQue.add(new Message_Que(0, bid, message[0], message[1], message[2], message[3], message[4], message[5], message[6]));
        }
    }
    public static void getHQButtonedStatus() throws GameActionException {
        for (int i = 1; i < rc.getRoundNum(); i++) {
            for (Transaction tx : rc.getBlock(i)) {
                int[] mess = tx.getMessage();
                if (mess[0] == 3333 + i * 2 && mess[1] == 40) {

                    System.out.println(Arrays.toString(mess));
                    HQButtonedUp=mess[2];
                }
            }
        }
    }
    public static void sendDroneSwarmLoc(MapLocation loc, int bid) throws GameActionException {

        int teamSecret = rc.getRoundNum() * 2 + 3333;
        int[] message = new int[7];
        message[0] = teamSecret;
        message[1] = 88;
        message[2] = 0;
        message[3] = loc.x;
        message[4] = loc.y;
        message[5] = 0;
        message[6] = rc.getID();
        if (rc.canSubmitTransaction(message, bid)) {
            rc.submitTransaction(message, bid);
            System.out.println("ENEMY HQ LOC SENT");
            messageQue.add(new Message_Que(0, bid, message[0], message[1], message[2], message[3], message[4], message[5], message[6]));
        }
    }
    public static MapLocation getDroneSwarmLoc() throws GameActionException {
        for (int i = 1; i < rc.getRoundNum(); i++) {
            System.out.println("BLOCKCHAIN");
            for (Transaction tx : rc.getBlock(i)) {
                int[] mess = tx.getMessage();
                if (mess[0] == 3333 + i * 2 && mess[1] == 88) {
                    System.out.println("GOT MESSAGE ENEMY HQ");
                    System.out.println(Arrays.toString(mess));
                    return new MapLocation(mess[3], mess[4]);
                }
            }
        }
        return null;
    }
}
