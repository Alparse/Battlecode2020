package Generation3_1;

import battlecode.common.MapLocation;

import java.util.Objects;

class Location_Que implements Comparable<Location_Que> {
    private MapLocation map_location;
    private int priority;

    public Location_Que(MapLocation map_location, int priority) {
        this.map_location= map_location;
        this.priority=priority;
    }

    public MapLocation getMap_location() {
        return map_location;
    }

    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(map_location, priority);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (this.getClass() != other.getClass()) {
            return false;
        }
        return (this.priority == ((Location_Que) other).priority);
    }

    public String toString() {
        return "Prioritized List{" +
                "Location='" + map_location + '\'' +
                "Priority='"+ priority+'\''+
                '}';
    }

    @Override
    public int compareTo(Location_Que Location_Que) {
        return Double.compare(this.getPriority(), Location_Que.getPriority());
    }

}
