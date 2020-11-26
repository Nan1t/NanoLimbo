package ru.nanit.limbo.util;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class UuidUtil {

    private UuidUtil(){}

    public static UUID getOfflineModeUuid(String username){
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username)
                .getBytes(StandardCharsets.UTF_8));
    }

}
