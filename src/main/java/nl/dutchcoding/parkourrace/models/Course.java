package nl.dutchcoding.parkourrace.models;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Course {

    private final String name;
    private Location startLocation;
    private final List<Location> checkpoints;
    private Location finishLocation;

    public Course(String name) {
        this.name = name;
        this.checkpoints = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public List<Location> getCheckpoints() {
        return checkpoints;
    }

    public void addCheckpoint(Location location) {
        checkpoints.add(location);
    }

    public Location getFinishLocation() {
        return finishLocation;
    }

    public void setFinishLocation(Location finishLocation) {
        this.finishLocation = finishLocation;
    }

    public boolean isValid() {
        return startLocation != null && finishLocation != null;
    }

    public int getCheckpointCount() {
        return checkpoints.size();
    }
}
