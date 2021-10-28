package ru.nanit.limbo.util;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class UuidUtil {

    private UuidUtil() {}

    public static UUID getOfflineModeUuid(String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username)
                .getBytes(StandardCharsets.UTF_8));
    }

    public static UUID fromString(String str) {
        if(str.contains("-")) return UUID.fromString(str);
        return UUID.fromString(str.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
    }

}
