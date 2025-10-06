package nl.dutchcoding.parkourrace.models;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ParkourSession {

    private final UUID playerId;
    private final Course course;
    private final long startTime;
    private int currentCheckpoint;
    private Location lastCheckpointLocation;
    private boolean finished;
    private ItemStack[] savedInventory;
    private ItemStack[] savedArmor;

    public ParkourSession(UUID playerId, Course course) {
        this.playerId = playerId;
        this.course = course;
        this.startTime = System.currentTimeMillis();
        this.currentCheckpoint = -1;
        this.lastCheckpointLocation = course.getStartLocation();
        this.finished = false;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Course getCourse() {
        return course;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    public int getCurrentCheckpoint() {
        return currentCheckpoint;
    }

    public void reachCheckpoint(int checkpointIndex, Location location) {
        this.currentCheckpoint = checkpointIndex;
        this.lastCheckpointLocation = location;
    }

    public void setSavedInventory(ItemStack[] inventory) {
        this.savedInventory = inventory;
    }

    public ItemStack[] getSavedInventory() {
        return savedInventory;
    }

    public void setSavedArmor(ItemStack[] armor) {
        this.savedArmor = armor;
    }

    public ItemStack[] getSavedArmor() {
        return savedArmor;
    }

    public Location getLastCheckpointLocation() {
        return lastCheckpointLocation;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
