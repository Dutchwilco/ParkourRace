package nl.dutchcoding.parkourrace.managers;

import nl.dutchcoding.parkourrace.ParkourRace;
import nl.dutchcoding.parkourrace.models.Course;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CourseManager {

    private final ParkourRace plugin;
    private File coursesFile;
    private FileConfiguration coursesConfig;
    private final Map<String, Course> courses;
    private final Map<Location, String> plateLocations; // Location -> CourseName

    public CourseManager(ParkourRace plugin) {
        this.plugin = plugin;
        this.courses = new HashMap<>();
        this.plateLocations = new HashMap<>();
    }

    public void loadCourses() {
        coursesFile = new File(plugin.getDataFolder(), "courses.yml");
        if (!coursesFile.exists()) {
            try {
                coursesFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create courses.yml!");
                e.printStackTrace();
            }
        }

        coursesConfig = YamlConfiguration.loadConfiguration(coursesFile);

        if (coursesConfig.contains("courses")) {
            for (String courseName : coursesConfig.getConfigurationSection("courses").getKeys(false)) {
                Course course = new Course(courseName);

                // Load start location
                if (coursesConfig.contains("courses." + courseName + ".start")) {
                    Location start = deserializeLocation(coursesConfig.getString("courses." + courseName + ".start"));
                    course.setStartLocation(start);
                    registerPlateLocation(start, courseName);
                }

                // Load checkpoints
                if (coursesConfig.contains("courses." + courseName + ".checkpoints")) {
                    List<String> checkpoints = coursesConfig.getStringList("courses." + courseName + ".checkpoints");
                    for (String checkpointString : checkpoints) {
                        Location checkpoint = deserializeLocation(checkpointString);
                        course.addCheckpoint(checkpoint);
                        registerPlateLocation(checkpoint, courseName);
                    }
                }

                // Load finish location
                if (coursesConfig.contains("courses." + courseName + ".finish")) {
                    Location finish = deserializeLocation(coursesConfig.getString("courses." + courseName + ".finish"));
                    course.setFinishLocation(finish);
                    registerPlateLocation(finish, courseName);
                }

                courses.put(courseName, course);
            }
        }

        plugin.getLogger().info("Loaded " + courses.size() + " parkour course(s).");
    }

    public void saveCourses() {
        coursesConfig.set("courses", null);

        for (Course course : courses.values()) {
            String path = "courses." + course.getName();

            if (course.getStartLocation() != null) {
                coursesConfig.set(path + ".start", serializeLocation(course.getStartLocation()));
            }

            List<String> checkpoints = new ArrayList<>();
            for (Location checkpoint : course.getCheckpoints()) {
                checkpoints.add(serializeLocation(checkpoint));
            }
            coursesConfig.set(path + ".checkpoints", checkpoints);

            if (course.getFinishLocation() != null) {
                coursesConfig.set(path + ".finish", serializeLocation(course.getFinishLocation()));
            }
        }

        try {
            coursesConfig.save(coursesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save courses.yml!");
            e.printStackTrace();
        }
    }

    public void addCourse(Course course) {
        courses.put(course.getName(), course);

        // Register plate locations
        registerPlateLocation(course.getStartLocation(), course.getName());
        for (Location checkpoint : course.getCheckpoints()) {
            registerPlateLocation(checkpoint, course.getName());
        }
        registerPlateLocation(course.getFinishLocation(), course.getName());

        saveCourses();
    }

    public void deleteCourse(String name) {
        Course course = courses.remove(name);
        if (course != null) {
            // Unregister plate locations
            plateLocations.remove(course.getStartLocation());
            course.getCheckpoints().forEach(plateLocations::remove);
            plateLocations.remove(course.getFinishLocation());

            saveCourses();
        }
    }

    public Course getCourse(String name) {
        return courses.get(name);
    }

    public Collection<Course> getAllCourses() {
        return courses.values();
    }

    public boolean courseExists(String name) {
        return courses.containsKey(name);
    }

    public String getCourseByPlateLocation(Location location) {
        return plateLocations.get(location);
    }

    private void registerPlateLocation(Location location, String courseName) {
        if (location != null) {
            plateLocations.put(location, courseName);
        }
    }

    private String serializeLocation(Location loc) {
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();
    }

    private Location deserializeLocation(String str) {
        String[] parts = str.split(",");
        return new Location(
                Bukkit.getWorld(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3]),
                Float.parseFloat(parts[4]),
                Float.parseFloat(parts[5])
        );
    }
}
