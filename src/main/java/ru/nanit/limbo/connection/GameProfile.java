package ru.nanit.limbo.connection;

import java.util.UUID;

public class GameProfile {

    private String username;
    private UUID uuid;

    public String getUsername() {
        return username;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
