package ru.nanit.limbo.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class Logger {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("hh:mm:ss");
    private static int debugLevel = 3;

    private Logger() {}

    public static void setLevel(int level) {
        debugLevel = level;
    }

    public static int getLevel() {
        return debugLevel;
    }

    public static void info(Object msg, Object... args) {
        print(Level.INFO, msg, null, args);
    }

    public static void debug(Object msg, Object... args) {
        print(Level.DEBUG, msg, null, args);
    }

    public static void warning(Object msg, Object... args) {
        print(Level.WARNING, msg, null, args);
    }

    public static void warning(Object msg, Throwable t, Object... args) {
        print(Level.WARNING, msg, t, args);
    }

    public static void error(Object msg, Object... args) {
        print(Level.ERROR, msg, null, args);
    }

    public static void error(Object msg, Throwable t, Object... args) {
        print(Level.ERROR, msg, t, args);
    }

    public static void print(Level level, Object msg, Throwable t, Object... args) {
        if (debugLevel >= level.getIndex()) {
            System.out.println(String.format("%s: %s", getPrefix(level), String.format(msg.toString(), args)));
            if (t != null) t.printStackTrace();
        }
    }

    private static String getPrefix(Level level) {
        return String.format("[%s] [%s]", getTime(), level.getDisplay());
    }

    private static String getTime() {
        return LocalTime.now().format(FORMATTER);
    }

    public enum Level {

        INFO ("INFO", 0),
        DEBUG ("DEBUG", 1),
        WARNING("WARNING", 2),
        ERROR("ERROR", 3);

        private final String display;
        private final int index;

        Level(String display, int index) {
            this.display = display;
            this.index = index;
        }

        public String getDisplay() {
            return display;
        }

        public int getIndex() {
            return index;
        }
    }
}
