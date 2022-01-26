package ru.nanit.limbo.server;

import ru.nanit.limbo.connection.ClientConnection;
import ru.nanit.limbo.util.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class Connections {

    private final Map<UUID, ClientConnection> connections;

    public Connections() {
        connections = new ConcurrentHashMap<>();
    }

    public Collection<ClientConnection> getAllConnections() {
        return Collections.unmodifiableCollection(connections.values());
    }

    public int getCount() {
        return connections.size();
    }

    public void addConnection(ClientConnection connection) {
        connections.put(connection.getUuid(), connection);
        Logger.info("Player %s connected (%s) [%s]", connection.getUsername(),
                connection.getAddress(), connection.getClientVersion());
    }

    public void removeConnection(ClientConnection connection) {
        connections.remove(connection.getUuid());
        Logger.info("Player %s disconnected", connection.getUsername());
    }
}
