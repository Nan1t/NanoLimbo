package ru.nanit.limbo.util;

public final class Colors {

    private static final char CHAR_FROM = '\u0026';
    private static final char CHAR_TO = '\u00A7';

    private Colors() {}

    public static String of(String text) {
        return text.replace(CHAR_FROM, CHAR_TO);
    }

}
