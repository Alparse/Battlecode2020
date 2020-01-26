package testRobot;

import battlecode.common.RobotInfo;

import java.util.Objects;

class Target_Que implements Comparable<Target_Que> {
    private RobotInfo robot;
    private int priority;

    public Target_Que(RobotInfo robot, int priority) {
        this.robot= robot;
        this.priority=priority;
    }

    public RobotInfo getRobot() {
        return robot;
    }

    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(robot, priority);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (this.getClass() != other.getClass()) {
            return false;
        }
        return (this.priority == ((Target_Que) other).priority);
    }

    public String toString() {
        return "Prioritized List{" +
                "Robot='" + robot + '\'' +
                "Priority='"+ priority+'\''+
                '}';
    }

    @Override
    public int compareTo(Target_Que Target_Que) {
        return Double.compare(this.getPriority(),Target_Que.getPriority());
    }

}
