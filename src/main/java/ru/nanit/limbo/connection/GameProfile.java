package ru.nanit.limbo.connection;

import java.util.UUID;

public class GameProfile {

    private UUID uuid;
    private String username;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
