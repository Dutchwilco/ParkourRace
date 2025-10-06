package nl.dutchcoding.parkourrace.utils;

public class TimeFormatter {

    public static String formatTime(long milliseconds) {
        long totalSeconds = milliseconds / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        long millis = (milliseconds % 1000) / 10;

        if (minutes > 0) {
            return String.format("%dm %02ds", minutes, seconds);
        } else {
            return String.format("%d.%02ds", seconds, millis);
        }
    }

    public static String formatTimeDetailed(long milliseconds) {
        long totalSeconds = milliseconds / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        long millis = (milliseconds % 1000) / 10;

        if (hours > 0) {
            return String.format("%dh %02dm %02ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %02d.%02ds", minutes, seconds, millis);
        } else {
            return String.format("%d.%02ds", seconds, millis);
        }
    }
}
