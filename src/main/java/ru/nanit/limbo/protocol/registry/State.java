package ru.nanit.limbo.protocol.registry;

import ru.nanit.limbo.protocol.Packet;
import ru.nanit.limbo.protocol.packets.*;
import ru.nanit.limbo.protocol.packets.login.*;
import ru.nanit.limbo.protocol.packets.play.*;
import ru.nanit.limbo.protocol.packets.status.PacketStatusPing;
import ru.nanit.limbo.protocol.packets.status.PacketStatusRequest;
import ru.nanit.limbo.protocol.packets.status.PacketStatusResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public enum State {

    HANDSHAKING(0){
        {
            serverBound.register(0x00, PacketHandshake::new);
        }
    },
    STATUS(1){
        {
            serverBound.register(0x01, PacketStatusPing::new);
            serverBound.register(0x00, PacketStatusRequest::new);
            clientBound.register(0x00, PacketStatusResponse::new);
            clientBound.register(0x01, PacketStatusPing::new);
        }
    },
    LOGIN(2){
        {
            serverBound.register(0x00, PacketLoginStart::new);
            serverBound.register(0x02, PacketLoginPluginResponse::new);
            clientBound.register(0x00, PacketDisconnect::new);
            clientBound.register(0x02, PacketLoginSuccess::new);
            clientBound.register(0x04, PacketLoginPluginRequest::new);
        }
    },
    PLAY(3){
        {
            serverBound.register(0x10, PacketKeepAlive::new);
            clientBound.register(0x10, PacketDeclareCommands::new);
            clientBound.register(0x24, PacketJoinGame::new);
            clientBound.register(0x30, PacketPlayerAbilities::new);
            clientBound.register(0x34, PacketPlayerPositionAndLook::new);
            clientBound.register(0x1F, PacketKeepAlive::new);
            clientBound.register(0x0E, PacketChatMessage::new);
            clientBound.register(0x0C, PacketBossBar::new);
            clientBound.register(0x32, PacketPlayerInfo::new);
        }
    };

    private static final Map<Integer, State> STATE_BY_ID = new HashMap<>();

    static {
        for (State registry : values()){
            STATE_BY_ID.put(registry.stateId, registry);
        }
    }

    private final int stateId;
    public final PacketRegistry serverBound = new PacketRegistry();
    public final PacketRegistry clientBound = new PacketRegistry();

    State(int stateId){
        this.stateId = stateId;
    }

    public static State getById(int stateId){
        return STATE_BY_ID.get(stateId);
    }

    public static class PacketRegistry {

        private final Map<Integer, Supplier<?>> packetsById = new HashMap<>();
        private final Map<Class<?>, Integer> packetIdByClass = new HashMap<>();

        public Packet getPacket(int packetId){
            Supplier<?> supplier = packetsById.get(packetId);
            return supplier == null ? null : (Packet) supplier.get();
        }

        public int getPacketId(Class<?> packetClass){
            return packetIdByClass.getOrDefault(packetClass, -1);
        }

        public void register(int packetId, Supplier<?> supplier){
            packetsById.put(packetId, supplier);
            packetIdByClass.put(supplier.get().getClass(), packetId);
        }

    }

}
