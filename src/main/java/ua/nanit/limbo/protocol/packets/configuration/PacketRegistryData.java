package ua.nanit.limbo.protocol.packets.configuration;

import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketOut;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.world.DimensionRegistry;

public class PacketRegistryData implements PacketOut {

    private DimensionRegistry dimensionRegistry;

    public void setDimensionRegistry(DimensionRegistry dimensionRegistry) {
        this.dimensionRegistry = dimensionRegistry;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeNamelessCompoundTag(dimensionRegistry.getCodec_1_20());
    }
}
