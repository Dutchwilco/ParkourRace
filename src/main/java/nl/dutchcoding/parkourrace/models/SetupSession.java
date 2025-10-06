package nl.dutchcoding.parkourrace.models;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SetupSession {

    private final UUID playerId;
    private final String courseName;
    private Location startLocation;
    private final List<Location> checkpoints;
    private Location finishLocation;

    public SetupSession(UUID playerId, String courseName) {
        this.playerId = playerId;
        this.courseName = courseName;
        this.checkpoints = new ArrayList<>();
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getCourseName() {
        return courseName;
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

    public Course toCourse() {
        Course course = new Course(courseName);
        course.setStartLocation(startLocation);
        checkpoints.forEach(course::addCheckpoint);
        course.setFinishLocation(finishLocation);
        return course;
    }
}
