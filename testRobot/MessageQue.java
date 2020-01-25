package testRobot;

class Message_Que {
    private int targetRobotID;
    private int bid;
    private int message0;
    private int message1;
    private int message2;
    private int message3;
    private int message4;
    private int message5;
    private int message6;
    private int message7;


    public Message_Que(int targetRobotID,int bid,int message0,int message1, int message2, int message3, int message4, int message5, int message6) {
        this.targetRobotID= targetRobotID;
        this.bid=bid;
        this.message0=message0;
        this.message1=message1;
        this.message2=message2;
        this.message3=message3;
        this.message4=message4;
        this.message5=message5;
        this.message6=message6;

    }

    public int gettargetRobotID() {
        return this.targetRobotID;
    }
    public void settargetRobotID(int RobotID) {
        this.targetRobotID=RobotID;
    }
    public int getBid() {
        return this.bid;
    }
    public void setBid(int bid) {
        this.bid=bid;
    }
    public int getMessage0() {
        return this.message0;
    }
    public void setMessage0(int message0) {
        this.message0=message0;
    }
    public int getMessage1() {
        return this.message1;
    }
    public void setMessage1(int message1) {
        this.message1=message1;
    }
    public int getMessage2() {
        return this.message2;
    }
    public void setMessage2(int message2) {
        this.message2=message2;
    }
    public int getMessage3() {
        return this.message3;
    }
    public void setMessage3(int message3) {
        this.message3=message3;
    }

    public int getMessage4() {
        return this.message4;
    }
    public void setMessage4(int message4) {
        this.message4=message4;
    }
    public int getMessage5() {
        return this.message5;
    }
    public void setMessage5(int message5) {
        this.message5=message5;
    }
    public int getMessage6() {
        return this.message6;
    }
    public void setMessage6(int message6) {
        this.message6=message6;
    }
    public String toString() {
        return "Message Que item{" +
                "Target Robot='" + targetRobotID + '\'' +
                "Bid='"+ bid+'\''+
                "Message0='"+ message0+'\''+
                "Message1='"+ message1+'\''+
                "Message2='"+ message2+'\''+
                "Message3='"+ message3+'\''+
                "Message4='"+ message4+'\''+
                "Message5='"+ message5+'\''+
                "Message6='"+ message6+'\''+
                '}';
    }


}
