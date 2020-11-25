package ru.nanit.limbo.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class Logger {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("hh:mm:ss");

    public static void info(Object msg, Object... args){
        print(Level.INFO, msg, null, args);
    }

    public static void info(Object msg, Throwable t, Object... args){
        print(Level.INFO, msg, t, args);
    }

    public static void warning(Object msg, Object... args){
        print(Level.WARNING, msg, null, args);
    }

    public static void warning(Object msg, Throwable t, Object... args){
        print(Level.WARNING, msg, t, args);
    }

    public static void error(Object msg, Object... args){
        print(Level.ERROR, msg, null, args);
    }

    public static void error(Object msg, Throwable t, Object... args){
        print(Level.ERROR, msg, t, args);
    }

    public static void print(Level level, Object msg, Throwable t, Object... args){
        System.out.println(String.format("%s: %s", getPrefix(level), String.format(msg.toString(), args)));
        if (t != null) t.printStackTrace();
    }

    private static String getPrefix(Level level){
        return String.format("[%s] [%s]", getTime(), level.getDisplay());
    }

    private static String getTime(){
        return LocalTime.now().format(FORMATTER);
    }

    public enum Level {

        INFO ("INFO"),
        WARNING("WARNING"),
        ERROR("ERROR");

        private final String display;

        Level(String display){
            this.display = display;
        }

        public String getDisplay() {
            return display;
        }
    }
}
