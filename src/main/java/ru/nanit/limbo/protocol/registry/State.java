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
            clientBound.register(0x00, PacketDisconnect::new);
            clientBound.register(0x02, PacketLoginSuccess::new);
        }
    },
    PLAY(3){
        {
            serverBound.register(0x10, PacketKeepAlive::new);
            clientBound.register(0x24, PacketJoinGame::new);
            clientBound.register(0x34, PacketPlayerPositionAndLook::new);
            clientBound.register(0x1F, PacketKeepAlive::new);
            clientBound.register(0x0E, PacketChatMessage::new);
            clientBound.register(0x0C, PacketBossBar::new);
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

    /*

    Temporary don't needed

    public static class PacketVersionRegistry {

        private final Map<Version, PacketIdRegistry<?>> MAPPINGS = new HashMap<>();

        public PacketIdRegistry<?> getRegistry(Version version){
            PacketIdRegistry<?> registry = MAPPINGS.get(version);
            return registry != null ? registry : MAPPINGS.get(version.getClosest(MAPPINGS.keySet()));
        }

        public <T extends Packet> void register(Version version, int packetId, Supplier<T> supplier){
            PacketIdRegistry<T> registry = (PacketIdRegistry<T>) MAPPINGS.computeIfAbsent(version, PacketIdRegistry::new);
            registry.register(packetId, supplier);
        }

        public static class PacketIdRegistry<T extends Packet> {

            private final Version version;
            private final Map<Integer, Supplier<T>> packetsById = new HashMap<>();
            private final Map<Class<?>, Integer> packetIdByClass = new HashMap<>();

            public PacketIdRegistry(Version version){
                this.version = version;
            }

            public Version getVersion(){
                return version;
            }

            public Packet getPacket(int packetId){
                Supplier<T> supplier = packetsById.get(packetId);
                return supplier == null ? null : supplier.get();
            }

            public int getPacketId(Class<?> packetClass){
                return packetIdByClass.getOrDefault(packetClass, -1);
            }

            public void register(int packetId, Supplier<T> supplier){
                packetsById.put(packetId, supplier);
                packetIdByClass.put(supplier.get().getClass(), packetId);
            }

        }

    }*/

}
