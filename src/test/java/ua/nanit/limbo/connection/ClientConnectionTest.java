/*
 * Copyright (C) 2020 Nan1t
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ua.nanit.limbo.connection;

import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ua.nanit.limbo.configuration.LimboConfig;
import ua.nanit.limbo.connection.pipeline.PacketDecoder;
import ua.nanit.limbo.connection.pipeline.PacketEncoder;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketSnapshot;
import ua.nanit.limbo.protocol.packets.PacketHandshake;
import ua.nanit.limbo.protocol.packets.login.PacketLoginPluginResponse;
import ua.nanit.limbo.protocol.packets.login.PacketLoginStart;
import ua.nanit.limbo.protocol.packets.login.PacketLoginSuccess;
import ua.nanit.limbo.protocol.registry.State;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.server.Connections;
import ua.nanit.limbo.server.LimboServer;
import ua.nanit.limbo.server.data.InfoForwarding;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClientConnectionTest {

    private static final int LOGIN_MESSAGE_ID = 17;
    private static final byte[] SECRET = "forwarding-test-secret".getBytes(StandardCharsets.UTF_8);

    @TempDir
    Path directory;

    private final List<EmbeddedChannel> channels = new ArrayList<>();
    private LimboServer server;
    private PacketSnapshot previousLoginSuccess;

    @BeforeEach
    void setUp() throws Exception {
        LimboConfig config = new LimboConfig(directory);
        config.load();
        server = new LimboServer();
        setField("config", config);
        setField("connections", new Connections(config));
        setField("packetHandler", new PacketHandler(server));

        previousLoginSuccess = PacketSnapshots.PACKET_LOGIN_SUCCESS;
        PacketLoginSuccess success = new PacketLoginSuccess(UUID.randomUUID(), "Limbo", UUID.randomUUID());
        PacketSnapshots.PACKET_LOGIN_SUCCESS = PacketSnapshot.of(success, Version.V1_20_2);
    }

    @AfterEach
    void tearDown() {
        try {
            channels.forEach(EmbeddedChannel::finishAndReleaseAll);
        } finally {
            PacketSnapshots.PACKET_LOGIN_SUCCESS = previousLoginSuccess;
        }
    }

    @Test
    void closesBeforeLoginAcknowledgedWithoutRetainingConnection() {
        ClientConnection connection = connection();
        startLogin(connection, "Player");
        assertEquals(State.LOGIN, connection.getState());
        assertEquals(1, server.getConnections().getCount());

        connection.getChannel().close();

        assertEquals(0, server.getConnections().getCount());
    }

    @Test
    void ignoresSecondLoginStartBeforeClosing() {
        ClientConnection connection = connection();
        startLogin(connection, "First");
        startLogin(connection, "Second");
        assertTrue(connection.isConnected());
        assertEquals("First", connection.getUsername());
        assertEquals(1, server.getConnections().getCount());

        connection.getChannel().close();

        assertEquals(0, server.getConnections().getCount());
    }

    @Test
    void closingOldOwnerDoesNotRemoveLiveReplacement() {
        ClientConnection oldConnection = connection();
        startLogin(oldConnection, "Player");
        oldConnection.updateState(State.PLAY);
        ClientConnection replacement = connection();
        startLogin(replacement, "Player");

        assertFalse(oldConnection.isConnected());
        assertTrue(replacement.isConnected());
        assertEquals(1, server.getConnections().getCount());
        assertTrue(server.getConnections().getAllConnections().contains(replacement));
    }

    @Test
    void keepsLegacyForwardedUuidOnLoginStart() {
        server.getConfig().getInfoForwarding().setType(InfoForwarding.Type.LEGACY);
        ClientConnection connection = connection();
        UUID forwardedUuid = UUID.randomUUID();
        EmbeddedChannel channel = (EmbeddedChannel) connection.getChannel();

        channel.writeInbound(new PacketHandshake(Version.V1_20_2, "host\0" + "10.0.0.1\0" + forwardedUuid, 25565,
                PacketHandshake.Intent.LOGIN));
        PacketLoginStart start = new PacketLoginStart();
        start.setUsername("Player");
        channel.writeInbound(start);

        assertEquals(forwardedUuid, connection.getUuid());
    }

    @Test
    void closesBeforeHandshakeWithoutException() {
        ClientConnection connection = connection();

        assertDoesNotThrow(() -> {
            connection.getChannel().close();
            ((EmbeddedChannel) connection.getChannel()).checkException();
        });
        assertEquals(0, server.getConnections().getCount());
    }

    @Test
    void ignoresSignedForwardingReplayBeforeProfileMutation() throws Exception {
        InfoForwarding forwarding = server.getConfig().getInfoForwarding();
        forwarding.setType(InfoForwarding.Type.MODERN);
        forwarding.setSecretKey(SECRET);
        ClientConnection connection = connection();
        connection.updateVersion(Version.V1_20_2);
        connection.updateState(State.LOGIN);
        connection.setVelocityLoginMessageId(LOGIN_MESSAGE_ID);
        UUID firstUuid = UUID.randomUUID();
        EmbeddedChannel channel = (EmbeddedChannel) connection.getChannel();

        channel.writeInbound(signedResponse(firstUuid, "First"));
        channel.writeInbound(signedResponse(UUID.randomUUID(), "Second"));

        assertEquals(firstUuid, connection.getUuid());
        assertEquals("First", connection.getUsername());
        assertTrue(connection.isConnected());
        assertEquals(1, server.getConnections().getCount());
        channel.close();
        assertEquals(0, server.getConnections().getCount());
    }

    private ClientConnection connection() {
        EmbeddedChannel channel = new EmbeddedChannel() {
            @Override
            protected SocketAddress remoteAddress0() {
                return new InetSocketAddress("127.0.0.1", 25565);
            }
        };
        channels.add(channel);
        ClientConnection connection = new ClientConnection(channel, server, new PacketDecoder(), new PacketEncoder());
        channel.pipeline().addLast(connection);
        return connection;
    }

    private void startLogin(ClientConnection connection, String username) {
        connection.updateVersion(Version.V1_20_2);
        connection.updateState(State.LOGIN);
        PacketLoginStart start = new PacketLoginStart();
        start.setUsername(username);
        ((EmbeddedChannel) connection.getChannel()).writeInbound(start);
    }

    private PacketLoginPluginResponse signedResponse(UUID uuid, String username) throws Exception {
        ByteMessage message = new ByteMessage(Unpooled.buffer());
        byte[] payload;
        try {
            message.writeVarInt(1);
            message.writeString("127.0.0.1");
            message.writeUuid(uuid);
            message.writeString(username);
            payload = message.toByteArray();
        } finally {
            message.release();
        }
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET, "HmacSHA256"));
        byte[] signature = mac.doFinal(payload);
        byte[] data = new byte[signature.length + payload.length];
        System.arraycopy(signature, 0, data, 0, signature.length);
        System.arraycopy(payload, 0, data, signature.length, payload.length);
        return new PacketLoginPluginResponse(LOGIN_MESSAGE_ID, true, data);
    }

    private void setField(String name, Object value) throws ReflectiveOperationException {
        Field field = LimboServer.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(server, value);
    }
}
